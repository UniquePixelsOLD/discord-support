package net.uniquepixels.support.ticket;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.uniquepixels.support.mongo.DefaultSubscriber;
import net.uniquepixels.support.mongo.MongoConnection;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
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

    public void createTicket(Member member, SupportType type, Consumer<ForumChannel> channelConsumer) throws IllegalStateException {

        ForumChannel forumChannel = this.guild.getForumChannelById(TICKET_CHANNEL_ID);

        if (forumChannel == null)
            throw new IllegalStateException("ForumChannel is null - may invalid id?");

        Optional<ForumTag> optionalForumTag = this.getBySupportType(type, forumChannel);

        if (optionalForumTag.isEmpty())
            throw new IllegalStateException("ForumTag is null - may not found?");

        forumChannel.createForumPost("#", MessageCreateData.fromEmbeds(this.getTicketCreateEmbed(member, type))).setTags(optionalForumTag.get()).queue(forumPost -> {

            forumPost.getThreadChannel().addThreadMember(member).queue();

            TicketEntry entry = new TicketEntry(member.getId(), type, null);
            this.saveForumPost(entry);

        });

        channelConsumer.accept(forumChannel);
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
