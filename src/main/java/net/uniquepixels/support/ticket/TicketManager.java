package net.uniquepixels.support.ticket;

import com.mongodb.reactivestreams.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.uniquepixels.support.mongo.DefaultSubscriber;
import net.uniquepixels.support.mongo.MongoConnection;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TicketManager {

    private final static String TICKET_CHANNEL_ID = "1213110064181809202";
    private final static String SUPPORT_MANAGER_CHANNEL_ID = "1195373369516888134";
    private final MongoConnection mongoConnection;

    private final Guild guild;

    public TicketManager(MongoConnection mongoConnection, Guild guild) {
        this.mongoConnection = mongoConnection;
        this.guild = guild;
    }

    private void getTicketCount(Consumer<Long> consumer) {

        this.mongoConnection.getTicketCollection().countDocuments().subscribe(new DefaultSubscriber<>(aLong -> {

            consumer.accept(aLong.orElse(-1L));

        }, 1L));
    }

    private void sendCreatedNewTicketNotification(Guild guild, ThreadChannel channel, SupportType type) {

        TextChannel notificationChannel = guild.getTextChannelById(SUPPORT_MANAGER_CHANNEL_ID);

        notificationChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("Benachrichtigung")
                        .setDescription("Ein neues Ticket wurde in " + channel.getAsMention() + " erstellt!")
                        .addField("Grund", type.getReason(), false)
                        .setTimestamp(Instant.now())
                        .build()
        ).queue();

    }

    public void createTicket(Member member, SupportType type, Consumer<ThreadChannel> channelConsumer) throws IllegalStateException {

        TextChannel textChannel = this.guild.getTextChannelById(TICKET_CHANNEL_ID);

        if (textChannel == null)
            throw new IllegalStateException("ForumChannel is null - may invalid id?");


        this.getTicketCount(count -> {

            textChannel.createThreadChannel(type.name().toLowerCase() + "-" + count, true)
                    .queue(threadChannel -> {
                        threadChannel.sendMessage(member.getAsMention()).addEmbeds(this.getTicketCreateEmbed(member, type)).queue();

                        threadChannel.addThreadMember(member).queue();

                        threadChannel.getPermissionContainer().getManager()
                                .putMemberPermissionOverride(member.getIdLong(),
                                        List.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS),
                                        List.of()).queue();

                        TicketEntry entry = new TicketEntry(member.getId(), type, null);
                        this.saveThreadChannel(entry);

                        threadChannel.sendMessage("")
                                .addActionRow(Button.danger("lock-ticket", "Ticket sperren"),
                                        Button.danger("close-ticket", "Ticket schließen"),
                                        Button.secondary("archive-ticket", "Ticket archivieren")).queue();


                        channelConsumer.accept(threadChannel);
                        this.sendCreatedNewTicketNotification(guild, threadChannel, type);
                    });

        });
    }

    private void saveThreadChannel(TicketEntry entry) {
        Executors.newSingleThreadExecutor().submit(() -> {
            MongoCollection<TicketEntry> tickets = this.mongoConnection.getDatabase().getCollection("tickets", TicketEntry.class);
            tickets.insertOne(entry).subscribe(new DefaultSubscriber<>(insertOneResult -> {

            }, 1));
        });
    }

    private MessageEmbed getTicketCreateEmbed(Member member, SupportType type) {
        return new EmbedBuilder()
                .setAuthor(member.getEffectiveName(), member.getEffectiveAvatarUrl())
                .setTitle("Support Ticket")
                .setColor(Color.cyan)
                .setDescription(member.getEffectiveName() + " created a support tickets due to a *" + type.name() + "*.")
                .build();
    }
}
