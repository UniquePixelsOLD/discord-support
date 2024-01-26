package net.uniquepixels.support.ticket.management;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.uniquepixels.support.api.buttons.Button;
import net.uniquepixels.support.ticket.SupportType;

public class RequestSupportButton implements Button {
    @Override
    public String id() {
        return "request-support";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        StringSelectMenu.Builder builder = StringSelectMenu.create("request-support");

        for (SupportType value : SupportType.values()) {
            builder.addOption(value.getReason(), value.name());
        }

        event.reply("Bitte wähle eine Kategorie aus!")
                .addActionRow(builder.build()).setEphemeral(true).queue();

    }
}
