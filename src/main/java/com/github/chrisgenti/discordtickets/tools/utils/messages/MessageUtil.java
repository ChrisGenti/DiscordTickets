package com.github.chrisgenti.discordtickets.tools.utils.messages;

public class MessageUtil {
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[1;31m";
    private static final String BLUE = "\033[1;34m";
    private static final String WHITE = "\033[0;37m";

    private static final char CUBE = '\u25A0';
    private static final char ARROW = '\u25B8';

    private static final String CUBE_COMPONENT = BLUE + CUBE;
    private static final String CUBE_COMPONENT_LINE = CUBE_COMPONENT + " " + CUBE_COMPONENT + " " + CUBE_COMPONENT;

    private static final String ARROW_COMPONENT = WHITE + ARROW + " ";

    public static final String LAUNCH_MESSAGE =
            "Starting Discord Tickets..." + "\n" + "\n" +
            CUBE_COMPONENT_LINE + "\n" + CUBE_COMPONENT_LINE + "   DISCORD TICKETS  v1.0" + "\n" +
            CUBE_COMPONENT_LINE + "\n" + "\n" +
            ARROW_COMPONENT + "os: " + System.getProperty("os.name") + ", " + System.getProperty("os.version") + " - " + System.getProperty("os.arch") + "\n" +
            ARROW_COMPONENT + "java: " + System.getProperty("java.version") + " - " + System.getProperty("java.vendor") + ", " + System.getProperty("java.vendor.url") + RESET + "\n";

    public static final String CONFIG_MESSAGE =
            "Discord Tickets launch result..." + "\n" + "\n" +
            ARROW_COMPONENT + "config location: config.json, %config_result%" + "\n" +
            ARROW_COMPONENT + "opened tickets: %opened_tickets%, closed tickets: %closed_tickets%" + "\n" + "\n" +
            WHITE +"Bot started in %mills%s" + "\n";

    public static final String MALFORMED_CONFIG_MESSAGE =
            "\n" + "\n" +
            RED + " ! Error while launching the Discord Tickets..." + "\n" +
            RED + "   The configuration file is malformed, for security" + "\n" +
            RED + "   and logistic reason the server will automatically" + "\n" +
            RED + "   stop in 5 seconds..." + "\n" + "\n" + RESET;

    public static String TICKET_CREATE_MESSAGE =
            WHITE + "%username% opened a ticket in the %ticket_category% category." + BLUE + "   ID: %ticket_id%" + RESET;

    public static String TICKET_CLOSE_MESSAGE =
            WHITE + "%admin_username% closed %username%'s ticket." + BLUE + "   ID: %ticket_id%" + RESET;
}
