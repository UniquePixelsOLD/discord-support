package net.uniquepixels.bot.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

public class MongoConnection {

    private final MongoDatabase database;

    public MongoConnection() {
        ConnectionString connString = new ConnectionString(
                "mongodb+srv://<username>:<password>@<cluster-address>/test?w=majority"
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient client = MongoClients.create(settings);


        this.database = client.getDatabase("test");

        Runtime.getRuntime().addShutdownHook(new Thread(client::close));

        this.createTestCollection();
    }

    public void createTestCollection() {

        this.getDatabase().createCollection("test").subscribe(new DefaultSubscriber<Void>(optional -> {

            if (optional.isEmpty()) {

                System.out.println("Something went wrong!");
                return;

            }

            System.out.println("created collection");

        }, 1));
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
