package net.uniquepixels.support.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import net.uniquepixels.support.ticket.TicketEntry;

public class MongoConnection {

    private final MongoDatabase database;

    public MongoConnection() {
        ConnectionString connString = new ConnectionString(System.getenv("MONGO_URL"));
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient client = MongoClients.create(settings);


        this.database = client.getDatabase("upd-support");

        Runtime.getRuntime().addShutdownHook(new Thread(client::close));

        this.createTestCollection();
    }

    public MongoCollection<TicketEntry> getTicketCollection() {
        return this.database.getCollection("tickets", TicketEntry.class);
    }

    public void createTestCollection() {

        this.getDatabase().createCollection("test").subscribe(new DefaultSubscriber<>(optional -> {

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
