package net.uniquepixels.support;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.uniquepixels.support.api.APIHandler;
import net.uniquepixels.support.config.ConfigCommand;
import net.uniquepixels.support.mongo.MongoConnection;
import net.uniquepixels.support.ticket.TicketManager;
import net.uniquepixels.support.ticket.management.*;

public class DiscordBot extends ListenerAdapter {
    public DiscordBot() throws InterruptedException {

        MongoConnection mongoConnection = new MongoConnection();


        JDABuilder builder = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"));

        APIHandler apiHandler = new APIHandler(builder);

        builder.setActivity(Activity.watching("Discord UP Template"));

        JDA bot = builder.build().awaitReady();

        Guild guild = bot.getGuilds().get(0);

        TicketManager ticketManager = new TicketManager(mongoConnection, guild);

        apiHandler.getCommandHandler().addSlashCommand(new ConfigCommand());
        apiHandler.getButtonHandler().getButtons().add(new RequestSupportButton());
        apiHandler.getButtonHandler().getButtons().add(new CloseTicketButton());
        apiHandler.getButtonHandler().getButtons().add(new LockTicketButton());
        apiHandler.getButtonHandler().getButtons().add(new ArchiveTicketButton());
        apiHandler.getSelectionMenuHandler().getStringSelectionMenus().add(new SelectCategoryMenu(ticketManager));

        apiHandler.getCommandHandler().updateCommands(guild);

    }

    public static void main(String[] args) throws InterruptedException {
        new DiscordBot();
    }
}