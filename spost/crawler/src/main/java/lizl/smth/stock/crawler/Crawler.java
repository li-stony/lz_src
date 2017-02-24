package lizl.smth.stock.crawler;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import lizl.smth.stock.log.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.bson.ByteBuf;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by cussyou on 17-2-23.
 */
public class Crawler implements Runnable{
    private static final String runningTable = "running";
    private static final String doneTable =  "done";
    private static final String idTable = "post_id";
    private static final String urlPre = "http://newsmth.net/nForum/#!article/Stock/";

    private String cookie = null;
    private long maxId;
    private MongoClient dbClient;
    private MongoDatabase db;
    public Crawler(String dbUrl) {
        MongoClientURI connectionString = new MongoClientURI(dbUrl);
        dbClient = new MongoClient(connectionString);
        db = dbClient.getDatabase("newsmth");
        init();
    }
    public void run() {
        while(true) {
            long next = nextId();
            Log.debug("nextId="+next);
            if(next > maxId) {
                break;
            }
            boolean ok = handleOne(next);
            if(!ok) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    protected long nextId() {
        Document query = new Document("_id", "next_id");
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("value", 1));

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.BEFORE);
        options.upsert(true);
        Document result = db.getCollection(idTable).findOneAndUpdate(query, update, options);
        return result.getLong("value");

    }
    private boolean handleOne(long next) {
        // save to running first
        Bson filter = Filters.eq("_id", next);
        Bson update = new Document("$set",
                new Document().append("_id", next).append("time", new Date())
        );

        UpdateOptions options = new UpdateOptions().upsert(true);
        db.getCollection(runningTable).updateOne(filter, update, options);
        // do http request
        String url = urlPre+next;
        try {
            Log.debug(url);
            HttpGet get = new HttpGet(url);
            get.setHeader("Cookie", cookie);
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(get);
            String status = response.getStatusLine().toString();
            Log.debug(status);
            String contentType = response.getEntity().getContentType().getValue();
            contentType = contentType.toLowerCase();
            int ind = contentType.indexOf("charset");
            String charset = "utf-8";
            if(ind > 0) {
                charset = contentType.substring(ind+"charset=".length());
            }
            String text = "";
            long len = response.getEntity().getContentLength();
            if(len > 0) {
                byte[] buf = new byte[(int)len];
                response.getEntity().getContent().read(buf);
                text = new String(buf, 0, (int)len, charset);
            } else {
                byte[] buf = new byte[4096];
                byte[] content = new byte[1024000];
                InputStream in = response.getEntity().getContent();
                int readBytes= 0;
                while(true) {
                    int ret = in.read(buf);
                    if(ret > 0) {
                        System.arraycopy(buf, 0, content, readBytes, ret);
                        readBytes+=ret;
                    } else {
                        break;
                    }
                }
                text = new String(content, 0, readBytes, charset);
            }

            Log.debug(text);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode>=300){
                return false;
            }
            // save to done
            update = new Document("$set",
                    new Document().append("_id", next)
                            .append("time", new Date())
                            .append("html", text)
            );

            options = new UpdateOptions().upsert(true);
            db.getCollection(doneTable).updateOne(filter, update, options);
            // delete running
            db.getCollection(runningTable).deleteMany(filter);
            return true;
        }catch (Exception e) {
            Log.debug(e.getMessage());
            e.printStackTrace();
            return  false;
        }
    }

    private void init() {
        Document query = new Document("_id", "max_id");
        Document result = db.getCollection(idTable).find(query).first();
        maxId = result.getLong("value");
        Log.debug("maxId:"+maxId);

        Bson filter = Filters.eq("_id", "cookie");
        result = db.getCollection(idTable).find(filter).first();
        cookie = result.getString("value");

    }
}
