package com.github.chrisgenti.discordtickets.listeners.modals;

import com.github.chrisgenti.discordtickets.managers.TicketManager;
import com.github.chrisgenti.discordtickets.objects.Ticket;
import com.github.chrisgenti.discordtickets.tools.enums.TicketType;
import com.github.chrisgenti.discordtickets.DiscordTickets;
import com.github.chrisgenti.discordtickets.managers.mongo.MongoManager;
import com.github.chrisgenti.discordtickets.tools.utils.messages.MessageUtil;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.listener.interaction.ModalSubmitListener;
import org.tinylog.Logger;

import java.awt.*;
import java.time.Instant;
import java.util.Date;

public class ModalListener implements ModalSubmitListener {
    private final MongoManager mongoManager;
    private final TicketManager ticketManager;

    public ModalListener(DiscordTickets discord) {
        this.mongoManager = discord.getMongoManager();
        this.ticketManager = discord.getTicketManager();
    }

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        User user = event.getModalInteraction().getUser(); String customID = event.getModalInteraction().getCustomId().replace("_modal", "");

        TicketType ticketType = TicketType.getByCustomID(customID);
        if (ticketType == null)
            return;

        if (event.getModalInteraction().getServer().isEmpty())
            return;
        Server server = event.getModalInteraction().getServer().get();

        if (ticketManager.getTicketsByUser(user.getIdAsString()).size() == 2) {
            event.getInteraction().createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(
                            new EmbedBuilder()
                                    .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                    .setDescription("You have reached your ticket limit.")
                                    .setColor(Color.RED)
                    ).respond();
            return;
        }

        if (server.getChannelCategoriesByName(ticketType.getCategory()).stream().findFirst().isEmpty())
            return;
        ChannelCategory category = server.getChannelCategoriesByName(ticketType.getCategory()).stream().findFirst().get();

        Role role = null;
        if (server.getRolesByName("SUPPORT").stream().findFirst().isPresent())
            role = server.getRolesByName("SUPPORT").stream().findFirst().get();

        String username = null;
        if (event.getModalInteraction().getTextInputValueByCustomId("username").isPresent())
            username = event.getModalInteraction().getTextInputValueByCustomId("username").get();

        String description = null;
        if (event.getModalInteraction().getTextInputValueByCustomId("description").isPresent())
            description = event.getModalInteraction().getTextInputValueByCustomId("description").get();

        String reportedUsername = null;
        if ((ticketType == TicketType.PLAYER_REPORTS || ticketType == TicketType.STAFF_REPORTS) && event.getModalInteraction().getTextInputValueByCustomId("reported_username").isPresent())
            reportedUsername = event.getModalInteraction().getTextInputValueByCustomId("reported_username").get();

        int number = ticketManager.getLastNumber() + 1; MessageBuilder builder = reportedUsername == null ? this.createMessage(ticketType, role, username, description) : this.createMessage(ticketType, role, username, reportedUsername, description);
        server.createTextChannelBuilder()
                .setCategory(category)
                .setName(
                        "ticket-{username}-{id}"
                                .replace("{username}", user.getName())
                                .replace("{id}", String.valueOf(number))
                )
                .addPermissionOverwrite(server.getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build())
                .addPermissionOverwrite(event.getModalInteraction().getUser(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.SEND_MESSAGES).build())
                .create().whenComplete((var, throwable) -> {
                    if (var.getCurrentCachedInstance().isEmpty())
                        return;
                    TextChannel channel = var.getCurrentCachedInstance().get();

                    builder.send(channel);
                    event.getInteraction().createImmediateResponder()
                            .setAllowedMentions(new AllowedMentionsBuilder().build())
                            .addEmbed(
                                    new EmbedBuilder()
                                            .setAuthor(ticketType.getLabel() + " Ticket", "", "https://i.imgur.com/s5k4che.png")
                                            .setDescription("New ticket created: <#" + channel.getIdAsString() + ">")
                                            .setColor(Color.GREEN)

                            ).setFlags(MessageFlag.EPHEMERAL).respond();

                    Ticket ticket = new Ticket(number, ticketType, user.getIdAsString(), channel.getIdAsString(), Date.from(Instant.now()));
                    mongoManager.createTicket(ticket); ticketManager.getTicketCache().add(ticket); ticketManager.setLastNumber(number);

                    Logger.info(
                            MessageUtil.TICKET_CREATE_MESSAGE
                                    .replace("%username%", user.getDisplayName(server))
                                    .replace("%ticket_category%", ticketType.getLabel())
                                    .replace("%ticket_id%", ticket.getId() + "")
                    );
                });
    }

    private MessageBuilder createMessage(TicketType ticketType, Role role, String username, String description) {
        String mentionLine = "";
        if (role != null)
            mentionLine = "**" + role.getMentionTag() + "** \n \n";

        return new MessageBuilder()
                .setAllowedMentions(
                        new AllowedMentionsBuilder()
                                .setMentionRoles(true)
                                .build()
                )
                .setEmbed(
                        new EmbedBuilder()
                                .setAuthor(ticketType.getLabel() + " Ticket", "", "https://i.imgur.com/s5k4che.png")
                                .setDescription(
                                        "**What is your in-game name? **" + "\n" + username + "\n" + "\n" +
                                        "**What happened? **" + "\n" + description + "\n" + "\n" + mentionLine
                                )
                                .setFooter("\n" + "Thank you for making a support ticket, our staff team will be with you shortly! ")
                                .setColor(Color.ORANGE)
                );
    }

    private MessageBuilder createMessage(TicketType ticketType, Role role, String username, String reportedUsername, String description) {
        String mentionLine = "";
        if (role != null)
            mentionLine = "**" + role.getMentionTag() + "** \n \n";

        String line = ticketType == TicketType.STAFF_REPORTS ? "What is the staffer's in-game username?" : "What is the player's in-game username?";
        return new MessageBuilder()
                .setAllowedMentions(
                        new AllowedMentionsBuilder()
                                .setMentionRoles(true)
                                .build()
                )
                .setEmbed(
                        new EmbedBuilder()
                                .setAuthor(ticketType.getLabel() + " Ticket", "", "https://i.imgur.com/s5k4che.png")
                                .setDescription(
                                        "**What is your in-game name? **" + "\n" + username + "\n" + "\n" +
                                        "**" + line + "**" + "\n" + reportedUsername + "\n" + "\n" +
                                        "**What happened? **" + "\n" + description + "\n" + "\n" + mentionLine
                                )
                                .setFooter("\n" + "Thank you for making a support ticket, our staff team will be with you shortly!")
                                .setColor(Color.ORANGE)
                );
    }
}
