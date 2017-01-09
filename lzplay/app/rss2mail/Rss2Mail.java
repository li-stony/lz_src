package rss2mail;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import models.FeedsModel;
import models.RssConfigModel;
import org.apache.commons.mail.HtmlEmail;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Transport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by cussyou on 16-12-29.
 */
public class Rss2Mail extends UntypedActor {

    private static boolean TEST = false;
    public static void startTask() {
        Akka.system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS),
                Duration.create(8, TimeUnit.HOURS),
                new Runnable() {
                    @Override
                    public void run() {
                        Rss2Mail.start();
                    }
                }, Akka.system().dispatcher());

    }

    private static void start() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        Date d = new Date();
        Logger.info(sdf.format(d) + " rss2mail start ");
        // read db
        RssConfigModel config = RssConfigModel.findOne(1);
        Logger.info(config.toJson().toString());
        List<FeedsModel> feeds = FeedsModel.list();
        int index = 0;
        LzMailClient mail = new LzMailClient();

        for(FeedsModel feed: feeds){
            fetch(feed._id, feed.rss, feed.last_update, config, mail);
            index++;
            if(TEST){
                break;
            }
        }
        d = new Date();
        Logger.info(sdf.format(d) + " rss2mail end");

    }
    private static void fetch(long _id, String rssUrl, long lastUpdate, RssConfigModel config, LzMailClient mail){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Logger.info(sdf.format(new Date())+" checking: "+_id+" "+rssUrl);

        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(rssUrl)));
            //Logger.info(feed.getTitle());
            //Logger.info(feed.getAuthor());
            //Logger.info(feed.toString());
            List<SyndEntry> items = feed.getEntries();
            Collections.reverse(items);
            for(SyndEntry item: items) {
                Date pubDate = item.getPublishedDate();
                if(pubDate == null){
                    pubDate = item.getUpdatedDate();
                }
                if(pubDate == null) {
                    pubDate = feed.getPublishedDate();
                }
                if(pubDate == null) {
                    pubDate = new Date();
                    pubDate.setTime(System.currentTimeMillis());
                    // only once every day
                    pubDate.setHours(23);
                    pubDate.setMinutes(59);
                    pubDate.setSeconds(59);
                }

                if( pubDate.getTime() > (lastUpdate+100)) {

                    StringBuilder sb = new StringBuilder();
                    //sb.append("<h1>[").append(feed.getTitle()).append("]").append(item.getTitle()).append("</h1>\r\n");
                    SimpleDateFormat sdfSource = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");

                    String updateStr = sdfSource.format(pubDate);
                    Logger.info("new article: "+item.getTitle()+", update:"+updateStr);
                    sb.append("<p>").append(updateStr).append("</p>\r\n");
                    sb.append("<p>").append(item.getLink()).append("<p><br>\r\n");
                    int contentLen = 0;
                    for(SyndContent content :item.getContents()){
                        sb.append(content.getValue());
                        sb.append("\r\n");
                        contentLen+= content.getValue().length();
                    }
                    if(contentLen == 0){
                        sb.append(item.getDescription()).append("\r\n");
                    }
                    String message = sb.toString();
                    // Logger.info(message);

                    // send mail
                    boolean ok = mail.sendMail(feed.getTitle()+"-"+item.getTitle(), message, config);
                    if(ok) {
                        updateDb(_id, feed.getLink(), feed.getTitle(), pubDate.getTime());
                    } else {
                        Logger.info("failed to send mail");
                        break;
                    }
                    if(TEST){
                        break;
                    }
                } else {
                    Logger.info("ignore: "+item.getTitle());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }
    public static String decodeFromB64(String b64) {
        byte[] data = Base64.getDecoder().decode(b64);
        try {
            String str = new String(data, 0, data.length, "UTF-8");
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String encodeToB64(String str) {
        try {
            byte[] data = str.getBytes("UTF-8");
            String b64 = Base64.getEncoder().encodeToString(data);
            return b64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateDb(long id, String home, String title, long lastUpdate){
        //
        Logger.info("updateDb: "+title+","+lastUpdate);
        if(home == null) home = "";
        if(title == null) title = "";
        FeedsModel.update(id, home, title, lastUpdate);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        Rss2Mail.start();
    }
}
