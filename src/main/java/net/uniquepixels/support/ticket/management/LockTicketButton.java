package net.uniquepixels.support.ticket.management;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.uniquepixels.support.api.buttons.Button;

public class LockTicketButton implements Button {

    @Override
    public String id() {
        return "lock-ticket";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        event.reply("Ticket ist nun gesperrt!").queue();
        threadChannel.getManager().setLocked(true).queue();
    }
}
