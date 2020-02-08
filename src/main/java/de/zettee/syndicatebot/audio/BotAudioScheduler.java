package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.zettee.syndicatebot.configuration.Configurator;
import de.zettee.syndicatebot.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BotAudioScheduler extends AudioEventAdapter {

    @Getter private final Guild guild;
    @Getter private final AudioPlayer player;
    @Getter private final BlockingQueue<AudioTrack> queue;
    @Getter private int volume;
    @Getter @Setter private boolean loop;

    @Getter @Setter private Message currentlyPlayingInfo;

    @Setter private Message playerPausedInfo;

    public BotAudioScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingDeque<>();

        this.volume = Configurator.ofGuild(guild).getVolume();
    }

    public void enqueue(AudioTrack track) {
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    public void next(){
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        player.setVolume(volume);
        player.setPaused(false);
    }
    public void next(AudioTrack track){
        player.startTrack(track, false);
        player.setVolume(volume);
        player.setPaused(false);
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
        if(playerPausedInfo != null && BotConnection.getTextChannel(guild).getLatestMessageId().equals(playerPausedInfo.getId())) {
            playerPausedInfo.editMessage(":pause_button: Sendung angehalten.").queue(this::setPlayerPausedInfo);
        } else {
            Messages.sendTextAndGet(":pause_button: Sendung angehalten.", BotConnection.getTextChannel(guild), this::setPlayerPausedInfo);
        }
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
        if(playerPausedInfo != null && BotConnection.getTextChannel(guild).getLatestMessageId().equals(playerPausedInfo.getId())) {
            playerPausedInfo.editMessage(":play_pause: Sendung wird fortgesetzt.").queue(this::setPlayerPausedInfo);
        } else {
            Messages.sendTextAndGet(":play_pause: Sendung wird fortgesetzt.", BotConnection.getTextChannel(guild), this::setPlayerPausedInfo);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        this.player.setVolume(this.volume);
        Messages.sendPlayerInfo(BotConnection.getTextChannel(guild), true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(loop) {
            next(track.makeClone());
            return;
        }

        if (endReason != null && endReason.mayStartNext) {
            next();
        }
    }

    public void setVolume(int volume) {
        this.volume = volume;
        player.setVolume(this.volume);
    }
}
