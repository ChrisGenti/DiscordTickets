package com.github.chrisgenti.discordtickets.tools.enums;

import net.fellbaum.jemoji.EmojiManager;
import org.javacord.api.entity.message.component.SelectMenuOption;

import java.util.Arrays;

public enum TicketType {
    PUNISHMENT("punishment", "TICKETS", "Punishment", ":printer:"),
    PAYMENTS("payments", "TICKETS", "Buycraft / Payment", ":dollar:"),
    STAFF_REPORTS("staff_reports", "STAFF REPORT TICKETS", "Staff Reports", ":page_facing_up:"),
    PLAYER_REPORTS("player_reports", "PLAYER REPORT TICKETS", "Player Reports", ":clipboard:"),
    BUG_REPORTS("bug_reports", "TICKETS", "Bug Reports", ":bug:"),
    GENERAL_QUESTIONS("general_questions", "TICKETS", "General Questions", ":gear:");

    private final String customID, category, label, emoji;
    TicketType(String customID, String category, String label, String emoji) {
        this.customID = customID; this.category = category; this.label = label; this.emoji = emoji;
    }

    public static TicketType getByCustomID(String value) {
        return Arrays.stream(values()).filter(var -> var.customID.equals(value)).findFirst().orElse(null);
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public SelectMenuOption createMenuOption() {
        return SelectMenuOption.create(this.label, this.customID, "", EmojiManager.getByAlias(this.emoji).get().getEmoji());
    }
}
