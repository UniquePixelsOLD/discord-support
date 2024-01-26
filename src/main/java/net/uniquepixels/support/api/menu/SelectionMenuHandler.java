package net.uniquepixels.support.api.menu;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.uniquepixels.support.api.menu.entity.EntitySelectionMenu;
import net.uniquepixels.support.api.menu.string.StringSelectionMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SelectionMenuHandler extends ListenerAdapter {
    private final List<EntitySelectionMenu> entitySelectionMenus = new ArrayList<>();
    private final List<StringSelectionMenu> stringSelectionMenus = new ArrayList<>();

    public List<EntitySelectionMenu> getEntitySelectionMenus() {
        return this.entitySelectionMenus;
    }

    public List<StringSelectionMenu> getStringSelectionMenus() {
        return this.stringSelectionMenus;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        stringSelectionMenus.forEach(stringSelectionMenu -> {
            if (stringSelectionMenu.id().equals(event.getSelectMenu().getId()))
                stringSelectionMenu.onExecute(event);
        });
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        entitySelectionMenus.forEach(entitySelectionMenus -> {
            if (entitySelectionMenus.id().equals(event.getSelectMenu().getId()))
                entitySelectionMenus.onExecute(event);
        });
    }
}
