package com.github.chrisgenti.discordtickets.managers;

import com.github.chrisgenti.discordtickets.objects.Ticket;
import com.github.chrisgenti.discordtickets.DiscordTickets;
import com.github.chrisgenti.discordtickets.managers.mongo.MongoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketManager {
    private int lastNumber;
    private final List<Ticket> ticketCache;

    public TicketManager(DiscordTickets discord) {
        this.ticketCache = new ArrayList<>();

        MongoManager mongoManager = discord.getMongoManager();
        this.setLastNumber(mongoManager.ticketLastNumber()); this.ticketCache.addAll(mongoManager.ticketsToList());
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public Ticket getTicketByChannel(String channelID) {
        return this.ticketCache.stream().filter(var -> var.getChannelID().equals(channelID)).findFirst().orElse(null);
    }

    public List<Ticket> getTicketsByUser(String userID) {
        return this.ticketCache.stream().filter(var -> var.getUserID().equals(userID)).collect(Collectors.toList());
    }

    public List<Ticket> getTicketCache() {
        return ticketCache;
    }

    public void setLastNumber(int number) {
        this.lastNumber = number;
    }
}
