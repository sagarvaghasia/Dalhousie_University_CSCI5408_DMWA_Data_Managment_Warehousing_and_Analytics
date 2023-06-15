package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReuterStore
{
    public MongoDBConnection mongoDBConnection;
    public MongoCollection<Document> collection;

    public ReuterStore(String databaseString)
    {
        this.mongoDBConnection = new MongoDBConnection(databaseString);
        mongoDBConnection.setDatabase("ReuterDb");
    }

    public void store(Reuter reuter)
    {
        Document document = new Document("_id", new ObjectId());
        document.append("title", reuter.getTitle());
        document.append("text", reuter.getText());
        collection.insertOne(document);
    }

    public void storeAll(HashMap<String, List<Reuter>> allReuters)
    {
        for(Map.Entry<String, List<Reuter>> reuters: allReuters.entrySet())
        {
            this.collection = this.mongoDBConnection.getCollection(reuters.getKey());
            for(Reuter reuter: reuters.getValue())
            {
                this.store(reuter);
            }
        }
    }
}
