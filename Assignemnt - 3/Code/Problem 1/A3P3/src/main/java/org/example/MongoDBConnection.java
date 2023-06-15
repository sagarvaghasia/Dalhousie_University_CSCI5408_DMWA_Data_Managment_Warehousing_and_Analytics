package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoDBConnection
{
    public MongoClient client;
    public MongoDatabase database;
    public ConnectionString connectionString;
    public MongoDBConnection(String url)
    {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.maxConnectionIdleTime(60000);//set the max wait time in (ms)
        MongoClientOptions opts = builder.build();
        this.connectionString = new ConnectionString(url);
        this.client = MongoClients.create(connectionString);
    }

    // This method returns the mongo collection object
    public MongoCollection<Document> getCollection(String name){
        return this.database.getCollection(name);
    }

    // This method sets the mongo database object
    public void setDatabase(String name){
        this.database = client.getDatabase(name);
    }
}
