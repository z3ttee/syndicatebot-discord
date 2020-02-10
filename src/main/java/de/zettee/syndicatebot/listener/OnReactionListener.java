package de.zettee.syndicatebot.listener;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import de.zettee.syndicatebot.Core;
import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.messages.Reactions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnReactionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(Core.isDevmode()) return;

        if(!event.getReaction().isSelf() && !event.getReactionEmote().isEmote()) {
            String received = event.getReactionEmote().getEmoji();
            handle(received, event.getMessageId(), event.getGuild());
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if(Core.isDevmode()) return;

        if(!event.getReaction().isSelf() && !event.getReactionEmote().isEmote()) {
            String received = event.getReactionEmote().getEmoji();
            handle(received, event.getMessageId(), event.getGuild());
        }
    }

    private void handle(String received, String message, Guild guild) {
        if(Core.isDevmode()) return;

        GuildMusicManager musicManager = BotConnection.getGuildMusicManager(guild);
        if(message.equals(musicManager.scheduler.getCurrentlyPlayingInfo().getId())) {
            if (received.equals(Reactions.REACTION_PAUSE)) {
                musicManager.player.setPaused(!musicManager.player.isPaused());
                return;
            }
            if (received.equals(Reactions.REACTION_PREV)) {
                // TODO
                return;
            }
            if (received.equals(Reactions.REACTION_NEXT)) {
                musicManager.scheduler.next();
            }
        }
    }
}
