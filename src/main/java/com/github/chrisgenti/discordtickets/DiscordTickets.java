package com.github.chrisgenti.discordtickets;

import com.github.chrisgenti.discordtickets.listeners.CommandListener;
import com.github.chrisgenti.discordtickets.listeners.menus.MenuListener;
import com.github.chrisgenti.discordtickets.listeners.modals.ModalListener;
import com.github.chrisgenti.discordtickets.managers.TicketManager;
import com.github.chrisgenti.discordtickets.managers.mongo.MongoManager;
import com.github.chrisgenti.discordtickets.tools.ObjectTriple;
import com.github.chrisgenti.discordtickets.tools.enums.files.FileResult;
import com.github.chrisgenti.discordtickets.tools.utils.files.FileUtil;
import com.github.chrisgenti.discordtickets.tools.utils.messages.MessageUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.nio.file.Paths;
import java.text.DecimalFormat;

public class DiscordTickets {
    private final DecimalFormat decimalFormat = new DecimalFormat("##0,000");

    private DiscordApi discord;
    private MongoManager mongoManager;
    private TicketManager ticketManager;

    public static final String CONFIG_LOCATION = System.getProperty("config.location", "config.json");

    public void init() {
        /*
            LAUNCH MESSAGE
         */
        Logger.info(MessageUtil.LAUNCH_MESSAGE);

        /*
            LOAD
         */
        this.load();
    }

    private void load() {
        long mills = System.currentTimeMillis();

        ObjectTriple<FileResult, DiscordData, Exception> objectTriple = FileUtil.loadData(Paths.get(CONFIG_LOCATION));

        switch (objectTriple.left()) {
            case CREATED, EXISTING -> {
                /*
                    START
                 */
                this.start(mills, objectTriple.left(), objectTriple.mid()); break;
            }
            case MALFORMED -> {
                /*
                    SHUTDOWN
                 */
                this.shutdown(); break;
            }
        }
    }

    private void start(long mills, @NotNull FileResult result, @NotNull DiscordData data) {
        DiscordData.Discord discordData = data.discord(); DiscordData.Mongo mongoData = data.mongo();

        if (discordData.token().isEmpty()) {
            this.shutdown();
            return;
        }

        if (mongoData.hostname().isEmpty() || mongoData.username().isEmpty() || mongoData.password().isEmpty()) {
            this.shutdown();
            return;
        }

        /*
            DISCORD INSTANCE
         */
        this.discord = new DiscordApiBuilder()
                .setToken("MTE1ODc1MjcxMzA5MDI4OTY4NQ.GtV-Us.yBnbC-K-NYr6yErlDxx4oUATUuzaaQqXroYHEg")
                .addIntents(Intent.MESSAGE_CONTENT, Intent.GUILDS)
                .login()
                .join();

        /*
            MANAGERS INSTANCE
         */
        this.mongoManager = new MongoManager(mongoData.hostname(), mongoData.username(), mongoData.password());
        this.ticketManager = new TicketManager(this);

        /*
            CREATE COMMANDS
         */
        SlashCommand.with("message", "Send message for ticket management.")
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discord)
                .join();
        SlashCommand.with("close", "Close the ticket linked to this channel.")
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discord)
                .join();
        Logger.info("Successfully logged commands: [Message, Stop]");

        /*
            REGISTER LISTENERS
         */
        this.discord.addListener(new CommandListener(this));
        this.discord.addListener(new MenuListener());
        this.discord.addListener(new ModalListener(this));
        Logger.info("Successfully logged events: [SlashCommandCreateEvent, SelectMenuChooseEvent, ModalSubmitEvent]");

        /*
            CONFIG MESSAGE
         */
        Logger.info(
                MessageUtil.CONFIG_MESSAGE
                        .replace("%config_result%", result.reason())
                        .replace("%opened_tickets%", mongoManager.openedTicketsCount() + "")
                        .replace("%closed_tickets%", mongoManager.closedTicketsCount() + "")
                        .replace("%mills%", decimalFormat.format(System.currentTimeMillis() - mills))
        );
    }

    private void shutdown() {
        Logger.info(MessageUtil.MALFORMED_CONFIG_MESSAGE);

        try {
            Thread.sleep(5000);
        } catch (IllegalArgumentException | InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    public DiscordApi getDiscord() {
        return discord;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }
}
