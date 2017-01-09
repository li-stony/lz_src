package models;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.Id;
import play.Configuration;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cussyou on 16-12-29.
 */
public class FeedsModel {
    @Id
    public long _id;
    public String rss;
    public String home;
    public String title;
    public long last_update;

    private static MongoCollection feeds;
    public static MongoCollection coll() {
        if(feeds != null) {
            return feeds;
        }
        String host = Configuration.root().getString("mongoHost");
        MongoClient client  = new MongoClient(host);
        DB db = client.getDB("rss2mail");
        Jongo jongo = new Jongo(db);
        feeds = jongo.getCollection("feeds");
        return feeds;
    }

    public static List<FeedsModel> list() {
        List<FeedsModel> result = new ArrayList<FeedsModel>();
        Iterator<FeedsModel>  it = coll().find().sort("{_id:1}").as(FeedsModel.class).iterator();
        while (it.hasNext()){
            FeedsModel item = it.next();
            result.add(item);
        }
        return result;
    }

    public static void update(long id, String home, String title, long lastUpdate) {
        coll().update("{_id:#}", id).with("{$set:{title:#,home:#, last_update:#}}", title, home, lastUpdate);
    }

}
