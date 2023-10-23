package com.github.chrisgenti.discordtickets.managers.mongo;

import com.github.chrisgenti.discordtickets.objects.Ticket;
import com.github.chrisgenti.discordtickets.tools.utils.Util;
import com.github.chrisgenti.discordtickets.tools.enums.TicketType;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MongoManager {
    private final MongoClientSettings settings;

    public MongoManager(String hostname, String username, String password) {
        LoggerFactory.getLogger(MongoManager.class);

        this.settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb+srv://" + username + ":" + password + "@" + hostname + "/?retryWrites=true&w=majority"))
                .serverApi(
                        ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build()
                )
                .build();
    }

    public void createTicket(Ticket ticket) {
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> collection = database.getCollection("opened_tickets");

                Document document = new Document(); document.put("id", ticket.getId()); document.put("type", ticket.getTicketType().toString()); document.put("user_id", ticket.getUserID()); document.put("channel_id", ticket.getChannelID()); document.put("open_date", Util.formatDate(ticket.getOpenDate()));
                collection.insertOne(document);
            } catch (MongoException ignored) {}
        }
    }

    public void closeTicket(Ticket ticket) {
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> openCollection = database.getCollection("opened_tickets");
                MongoCollection<Document> closeConnection = database.getCollection("closed_tickets");

                Document searchDocument = new Document(); searchDocument.put("id", ticket.getId());
                openCollection.deleteOne(searchDocument);

                Document document = new Document(); document.put("id", ticket.getId()); document.put("type", ticket.getTicketType().toString()); document.put("user_id", ticket.getUserID()); document.put("channel_id", ticket.getChannelID()); document.put("open_date", Util.formatDate(ticket.getOpenDate())); document.put("close_date", Util.formatDate(ticket.getCloseDate()));
                closeConnection.insertOne(document);
            } catch (MongoException ignored) {}
        }
    }

    public int ticketLastNumber() {
        List<Integer> values = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> collection;

                collection = database.getCollection("opened_tickets");
                try (MongoCursor<Document> cursor = collection.find().cursor()) {
                    while (cursor.hasNext())
                        values.add(cursor.next().getInteger("id"));
                }

                collection = database.getCollection("closed_tickets");
                try (MongoCursor<Document> cursor = collection.find().cursor()) {
                    while (cursor.hasNext())
                        values.add(cursor.next().getInteger("id"));
                }
            } catch (MongoException ignored) {}
        }
        return values.isEmpty() ? 0 : Collections.max(values);
    }

    public List<Ticket> ticketsToList() {
        List<Ticket> values = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> collection = database.getCollection("opened_tickets");

                try (MongoCursor<Document> cursor = collection.find().cursor()) {
                    while (cursor.hasNext()) {
                        Document document = cursor.next();

                        values.add(new Ticket(
                                document.getInteger("id"), TicketType.valueOf(document.getString("type")), document.getString("user_id"), document.getString("channel_id"), Util.parseDate(document.getString("open_date"))
                        ));
                    }
                }
            } catch (MongoException ignored) {}
        }
        return values;
    }

    public int openedTicketsCount() {
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> collection = database.getCollection("opened_tickets");
                return Math.toIntExact(collection.countDocuments());
            } catch (MongoException ignored) {}
        }
        return 0;
    }

    public int closedTicketsCount() {
        try (MongoClient mongoClient = MongoClients.create(this.settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("discord_tickets");
                MongoCollection<Document> collection = database.getCollection("closed_tickets");
                return Math.toIntExact(collection.countDocuments());
            } catch (MongoException ignored) {}
        }
        return 0;
    }
}
