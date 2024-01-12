package net.uniquepixels.bot;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class DiscordBot {
    public DiscordBot() throws InterruptedException {

        JDABuilder builder = JDABuilder.createDefault("token");

        builder.setActivity(Activity.watching("Discord UP Template"));

        JDA bot = builder.build().awaitReady();
    }

    public static void main(String[] args) throws InterruptedException {
        new DiscordBot();
    }
}