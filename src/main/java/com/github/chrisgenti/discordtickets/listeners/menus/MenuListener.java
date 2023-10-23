package com.github.chrisgenti.discordtickets.listeners.menus;

import com.github.chrisgenti.discordtickets.tools.enums.TicketType;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

import java.util.Arrays;

public class MenuListener implements SelectMenuChooseListener {
    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        String customID = event.getSelectMenuInteraction().getCustomId();
        if (!customID.equals("tickets_menu"))
            return;

        if (event.getSelectMenuInteraction().getChosenOptions().stream().findFirst().isEmpty())
            return;
        String value = event.getSelectMenuInteraction().getChosenOptions().stream().findFirst().get().getValue();

        TicketType ticketType = TicketType.getByCustomID(value);
        if (ticketType == null)
            return;

        switch (ticketType) {
            case PLAYER_REPORTS:
                event.getInteraction().respondWithModal(value + "_modal", ticketType.getLabel() + " Ticket Model", Arrays.asList(
                        ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "username", "What is your in-game name?", true)
                        ),
                        ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "reported_username", "What is the staffer's in-game username?", true)
                        ),
                        ActionRow.of(
                                TextInput.create(TextInputStyle.PARAGRAPH, "description", "What happened? (Explain in detail)", true)
                        )
                ));
                break;
            case STAFF_REPORTS:
                event.getInteraction().respondWithModal(value + "_modal", ticketType.getLabel() + " Ticket Model", Arrays.asList(
                        ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "username", "What is your in-game name?", true)
                        ),
                        ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "reported_username", "What is the player's in-game username?", true)
                        ),
                        ActionRow.of(
                                TextInput.create(TextInputStyle.PARAGRAPH, "description", "What happened? (Explain in detail)", true)
                        )
                ));
                break;
            default:
                event.getInteraction().respondWithModal(value + "_modal", ticketType.getLabel() + " Ticket Model", Arrays.asList(
                        ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "username", "What is your in-game name?", true)
                        ),
                        ActionRow.of(
                                TextInput.create(TextInputStyle.PARAGRAPH, "description", "What happened? (Explain in detail)", true)
                        )
                ));
        }
    }
}
