package net.uniquepixels.support.ticket.management;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.uniquepixels.support.api.Roles;
import net.uniquepixels.support.api.buttons.Button;

import java.util.concurrent.TimeUnit;

public class CloseTicketButton implements Button {
    @Override
    public String id() {
        return "close-ticket";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        event.reply("Ticket ist nun geschlossen!").queue();

        threadChannel.getThreadMembers().stream().filter(threadMember -> !Roles.hasMemberRole(threadMember.getMember(), Roles.STAFF)).forEach(threadMember -> {
            threadChannel.removeThreadMember(threadMember.getMember()).queue();
        });

        threadChannel.getManager().setLocked(true).setArchived(true).queueAfter(1, TimeUnit.SECONDS);
    }
}
