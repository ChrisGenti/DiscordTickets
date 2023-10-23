package com.github.chrisgenti.discordtickets.listeners;

import com.github.chrisgenti.discordtickets.DiscordTickets;
import com.github.chrisgenti.discordtickets.managers.TicketManager;
import com.github.chrisgenti.discordtickets.objects.Ticket;
import com.github.chrisgenti.discordtickets.tools.enums.TicketType;
import com.github.chrisgenti.discordtickets.managers.mongo.MongoManager;
import com.github.chrisgenti.discordtickets.tools.utils.Util;
import com.github.chrisgenti.discordtickets.tools.utils.messages.MessageUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.tinylog.Logger;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

public class CommandListener implements SlashCommandCreateListener {
    private final DiscordApi discordAPI;
    private final MongoManager mongoManager;
    private final TicketManager ticketManager;

    public CommandListener(DiscordTickets discord) {
        this.discordAPI = discord.getDiscord();
        this.mongoManager = discord.getMongoManager();
        this.ticketManager = discord.getTicketManager();
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        String command = event.getSlashCommandInteraction().getCommandName();
        if (!command.equals("message") && !command.equals("close"))
            return;

        if (command.equals("message")) {
            if (!event.getSlashCommandInteraction().getChannel().isPresent())
                return;
            TextChannel channel = event.getSlashCommandInteraction().getChannel().get();

            if (!channel.getIdAsString().equals("1159103734752759938")) {
                event.getInteraction().createImmediateResponder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(
                                new EmbedBuilder()
                                        .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                        .setDescription("This channel is not enabled for tickets.")
                                        .setColor(Color.RED)
                        ).respond();
                return;
            }

            event.getInteraction().createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(
                            new EmbedBuilder()
                                    .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                    .setDescription("Main message sent successfully.")
                                    .setColor(Color.GREEN)
                    ).respond();
            this.createMessage().send(channel); return;
        }

        if (event.getSlashCommandInteraction().getChannel().isEmpty())
            return;
        TextChannel channel = event.getSlashCommandInteraction().getChannel().get();

        if (event.getInteraction().getServer().isEmpty())
            return;
        Server server = event.getInteraction().getServer().get();

        if (server.getChannelById(channel.getIdAsString()).isEmpty())
            return;
        ServerChannel serverChannel = server.getChannelById(channel.getIdAsString()).get();

        Ticket ticket = ticketManager.getTicketByChannel(serverChannel.getIdAsString());
        if (ticket == null) {
            event.getInteraction().createImmediateResponder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(
                            new EmbedBuilder()
                                    .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                    .setDescription("This is not a dedicated ticket channel.")
                                    .setColor(Color.RED)
                    ).respond();
            return;
        }
        User user = event.getInteraction().getUser(); User userTarget = discordAPI.getUserById(ticket.getUserID()).join();

        if (server.getMemberById(ticket.getUserID()).isPresent())
            userTarget = server.getMemberById(ticket.getUserID()).get();

        event.getInteraction().createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(
                        new EmbedBuilder()
                                .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                .setDescription("You have successfully closed this ticket.")
                                .setColor(Color.GREEN)
                ).respond();
        serverChannel.delete(); ticket.setCloseDate(Date.from(Instant.now())); ticketManager.getTicketCache().remove(ticket); mongoManager.closeTicket(ticket);

        userTarget.sendMessage(
                new EmbedBuilder()
                        .setAuthor("Discord Support Tickets  (" + ticket.getId() + ")", "", "https://i.imgur.com/s5k4che.png")
                        .setDescription(
                                "\n" + "\n" +
                                "**CLOSED BY:** " + user.getDisplayName(server) + "\n" +
                                "**CLOSED ON:** " + Util.formatDate(ticket.getCloseDate()) + "\n" + "\n" +
                                "__**NOTE**__ *There will be more updates soon.*"
                        )
                        .setThumbnail("https://i.imgur.com/s5k4che.png")
                        .setColor(Color.ORANGE)
        ).join();

        Logger.info(
                MessageUtil.TICKET_CLOSE_MESSAGE
                        .replace("%admin_username%", user.getDisplayName(server))
                        .replace("%username%", userTarget.getDisplayName(server))
                        .replace("%ticket_id%", ticket.getId() + "")
        );
    }

    private MessageBuilder createMessage() {
        return new MessageBuilder()
                .setEmbed(
                        new EmbedBuilder()
                                .setAuthor("Discord Support Tickets", "", "https://i.imgur.com/s5k4che.png")
                                .setDescription(
                                        "If you're in need of support from a staff member, please create a ticket! You can ask about any issues or questions you may have." +
                                        "\n" + "\n" +
                                        "Our staff will try to respond to you as quick as possible! Please refrain from pinging any staff."
                                )
                                .setThumbnail("https://i.imgur.com/s5k4che.png")
                                .setColor(Color.ORANGE)
                )
                .addComponents(
                        ActionRow.of(SelectMenu.createStringMenu("tickets_menu", "How can we help?", Arrays.asList(
                                TicketType.PUNISHMENT.createMenuOption(), TicketType.PAYMENTS.createMenuOption(), TicketType.STAFF_REPORTS.createMenuOption(), TicketType.PLAYER_REPORTS.createMenuOption(), TicketType.BUG_REPORTS.createMenuOption(), TicketType.GENERAL_QUESTIONS.createMenuOption()
                        )))
                );
    }
}
