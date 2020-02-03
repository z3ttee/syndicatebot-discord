package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.zettee.syndicatebot.configuration.Configurator;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BotAudioScheduler extends AudioEventAdapter {

    @Getter private final Guild guild;
    @Getter private final AudioPlayer player;
    @Getter private final BlockingQueue<AudioTrack> queue;
    @Getter private int volume;

    @Getter @Setter private Message currentlyPlayingInfo;

    public BotAudioScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingDeque<>();

        this.volume = Configurator.ofGuild(guild).getVolume();
    }

    public void enqueue(AudioTrack track) {
        if(player.getPlayingTrack() != null) {
            long timeMillis = 0;

            for(AudioTrack t : getQueue()) {
                timeMillis += t.getDuration();
            }

            timeMillis += (player.getPlayingTrack().getDuration()-player.getPlayingTrack().getPosition());

            Duration duration = Duration.ofMillis(timeMillis);
            long min = duration.toMinutes();
            long sec = duration.minusMinutes(min).getSeconds();

            BotConnection.getTextChannel(guild).sendMessage(
                    new MessageBuilder(":white_check_mark: Sendung zur Warteschlange :clipboard: hinzugefügt")
                            .setEmbed(new EmbedBuilder()
                                    .setTitle(track.getInfo().title,track.getInfo().uri)
                                    .setDescription(" ")
                                    .addField("# in Warteschlange", String.valueOf(getQueue().size()+1), false)
                                    .addField("sendet in ",min+":"+sec, false).build())
                            .build()
            ).queue();
        }

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
        this.player.setVolume(this.volume);
        BotConnection.sendPlayerInfo(BotConnection.getTextChannel(guild), true);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason != null && endReason.mayStartNext) {
            next();
        }
    }

    public void setVolume(int volume) {
        this.volume = volume;
        player.setVolume(this.volume);
    }
}
