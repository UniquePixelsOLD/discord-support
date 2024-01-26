package net.uniquepixels.support.ticket;

import com.mongodb.reactivestreams.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.uniquepixels.support.mongo.DefaultSubscriber;
import net.uniquepixels.support.mongo.MongoConnection;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class TicketManager {

    private final static String TICKET_CHANNEL_ID = "1195637835257106554";
    private final static String SUPPORT_MANAGER_CHANNEL_ID = "1195373369516888134";
    private final MongoConnection mongoConnection;

    private final Guild guild;

    public TicketManager(MongoConnection mongoConnection, Guild guild) {
        this.mongoConnection = mongoConnection;
        this.guild = guild;
    }

    private void getTicketCount(Consumer<Long> consumer) {

        AtomicLong atomicLong = new AtomicLong();

        this.mongoConnection.getTicketCollection().countDocuments().subscribe(new DefaultSubscriber<>(aLong -> {

            consumer.accept(aLong.orElse(-1L));

        }, 1L));
    }

    private void sendCreatedNewTicketNotification(Guild guild, ForumPost channel, SupportType type) {

        TextChannel notificationChannel = guild.getTextChannelById(SUPPORT_MANAGER_CHANNEL_ID);

        notificationChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("Benachrichtigung")
                        .setDescription("Ein neues Ticket wurde in " + channel.getThreadChannel().getAsMention() + " erstellt!")
                        .addField("Grund", type.getReason(), false)
                        .setTimestamp(Instant.now())
                        .build()
        ).queue();

    }

    public void createTicket(Member member, SupportType type, Consumer<ForumPost> channelConsumer) throws IllegalStateException {

        ForumChannel forumChannel = this.guild.getForumChannelById(TICKET_CHANNEL_ID);

        if (forumChannel == null)
            throw new IllegalStateException("ForumChannel is null - may invalid id?");

        Optional<ForumTag> optionalForumTag = this.getBySupportType(type, forumChannel);

        if (optionalForumTag.isEmpty())
            throw new IllegalStateException("ForumTag is null - may not found?");

        this.getTicketCount(count -> {
            forumChannel.createForumPost("#" + count.longValue(), MessageCreateData.fromEmbeds(this.getTicketCreateEmbed(member, type))).setTags(optionalForumTag.get()).queue(forumPost -> {

                forumPost.getThreadChannel().addThreadMember(member).queue();

                TicketEntry entry = new TicketEntry(member.getId(), type, null);
                this.saveForumPost(entry);

                forumPost.getThreadChannel().sendMessage("")
                        .addActionRow(Button.danger("lock-ticket", "Ticket sperren"), Button.danger("close-ticket", "Ticket schließen")).queue();


                channelConsumer.accept(forumPost);
                this.sendCreatedNewTicketNotification(guild, forumPost, type);

            });
        });
    }

    private void saveForumPost(TicketEntry entry) {
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

    private Optional<ForumTag> getBySupportType(SupportType type, ForumChannel forumChannel) {

        List<ForumTag> list = forumChannel.getAvailableTagsByName(type.name(), true);

        if (list.isEmpty())
            return Optional.empty();

        return Optional.of(list.get(0));
    }
}
