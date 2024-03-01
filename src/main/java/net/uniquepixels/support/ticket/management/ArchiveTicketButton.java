package net.uniquepixels.support.ticket.management;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.uniquepixels.support.api.Roles;
import net.uniquepixels.support.api.buttons.Button;

public class ArchiveTicketButton implements Button {
    @Override
    public String id() {
        return "archive-ticket";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();

        assert member != null;

        if (!Roles.hasMemberRole(member, Roles.STAFF)) {
            event.reply("Du hast keine Berechtigung dies zu tun!").setEphemeral(true).queue();
            return;
        }

        ThreadChannel threadChannel = event.getChannel().asThreadChannel();

        threadChannel.getManager().setInvitable(false).setArchived(true).queue();
        threadChannel.retrieveThreadMembers().stream().forEach(threadMember -> {
            if (!Roles.hasMemberRole(threadMember.getMember(), Roles.STAFF))
                threadChannel.removeThreadMember(threadMember.getMember()).queue();
        });


    }
}
