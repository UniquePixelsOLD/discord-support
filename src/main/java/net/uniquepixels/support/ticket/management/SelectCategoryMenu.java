package net.uniquepixels.support.ticket.management;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.uniquepixels.support.api.menu.string.StringSelectionMenu;
import net.uniquepixels.support.ticket.SupportType;
import net.uniquepixels.support.ticket.TicketManager;

public class SelectCategoryMenu implements StringSelectionMenu {

    private final TicketManager ticketManager;

    public SelectCategoryMenu(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    @Override
    public String id() {
        return "request-support";
    }

    @Override
    public void onExecute(StringSelectInteractionEvent event) {

        event.deferReply(true).queue();

        SelectOption selectOption = event.getSelectedOptions().get(0);

        SupportType supportType = SupportType.valueOf(selectOption.getValue().toUpperCase());

        Member member = event.getMember();


        this.ticketManager.createTicket(member, supportType, threadChannel -> {
            event.getHook().editOriginal("Das Ticket wurde in " + threadChannel.getAsMention() + " erstellt!").queue();
        });
    }
}
