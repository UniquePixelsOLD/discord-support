package net.uniquepixels.support.config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.uniquepixels.support.api.commands.slashcommands.SlashCommand;

public class ConfigCommand implements SlashCommand {

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("config", "Konfigurationen");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Support")
                .build())
                .addActionRow(Button.primary("request-support", "Support anfragen")).queue();

        event.reply("Send").setEphemeral(true).queue();
    }
}
