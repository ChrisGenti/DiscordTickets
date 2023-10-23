package com.github.chrisgenti.discordtickets.objects;

import com.github.chrisgenti.discordtickets.tools.enums.TicketType;

import java.util.Date;

public class Ticket {
    private final int id;
    private final TicketType ticketType;
    private final String userID, channelID;
    private final Date openDate;
    private Date closeDate;

    public Ticket(int id, TicketType ticketType, String userID, String channelID, Date openDate) {
        this.id = id;
        this.ticketType = ticketType;
        this.userID = userID;
        this.channelID = channelID;
        this.openDate = openDate;
        this.closeDate = null;
    }

    public int getId() {
        return id;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public String getUserID() {
        return userID;
    }

    public String getChannelID() {
        return channelID;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }
}
