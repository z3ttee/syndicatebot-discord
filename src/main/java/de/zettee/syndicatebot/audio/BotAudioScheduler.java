package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.zettee.syndicatebot.configuration.Configurator;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BotAudioScheduler extends AudioEventAdapter {

    @Getter private final Guild guild;
    @Getter private final AudioPlayer player;
    @Getter private final BlockingQueue<AudioTrack> queue;

    public BotAudioScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void enqueue(AudioTrack track) {
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    public void next(){
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        player.setVolume(Configurator.ofGuild(guild).getVolume());

        BotConnection.getTextChannel(guild).sendMessage("Next on: "+track.getInfo().title).queue();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason != null && endReason.mayStartNext) {
            next();
        }
    }
}
