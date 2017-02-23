package lizl.smth.stock.crawler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import lizl.smth.stock.log.Log;
import org.bson.Document;

import java.lang.annotation.Documented;

/**
 * Created by cussyou on 17-2-23.
 */
public class Crawler implements Runnable{
    private static final String runningTable = "running";
    private static final String doneTabe =  "done";
    private static final String idTable = "post_id";

    private long postId;
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
        handleOne();
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
    private void handleOne() {
        long next = nextId();
        Log.debug("nextId="+next);
        // save to running first

        // save to done at last
    }
    private void init() {
        Document query = new Document("_id", "max_id");
        Document result = db.getCollection(idTable).find(query).first();
        maxId = result.getLong("value");
        Log.debug("maxId:"+maxId);

    }
}
