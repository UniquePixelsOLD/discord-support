package net.uniquepixels.support.api.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface Button {

    String id();
    void onExecute(ButtonInteractionEvent event);

}
