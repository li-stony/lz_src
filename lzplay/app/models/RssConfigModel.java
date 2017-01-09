package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.Id;
import play.Configuration;

/**
 * Created by cussyou on 16-12-29.
 */
public class RssConfigModel {
    @Id
    public long _id;
    public String src_mail;
    public String src_pass;
    public String src_smtp;
    public String to_mail;
    public int ssl;

    public static RssConfigModel findOne(long id){
        RssConfigModel config = coll().findOne("{_id:#}", id).as(RssConfigModel.class);
        return config;
    }

    private static MongoCollection config;
    public static MongoCollection coll() {
        if(config != null) {
            return config;
        }
        String host = Configuration.root().getString("mongoHost");
        MongoClient client  = new MongoClient(host);
        DB db = client.getDB("rss2mail");
        Jongo jongo = new Jongo(db);
        config = jongo.getCollection("config");
        return config;
    }

    public JsonNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("_id", _id);
        node.put("src_mail", src_mail);
        node.put("src_pass", src_pass);
        node.put("src_smtp", src_smtp);
        node.put("to_mail", to_mail);
        node.put("ssl", ssl);
        return node;
    }
}
