package de.zettee.syndicatebot.audio.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.zettee.syndicatebot.audio.BotAudioScheduler;
import de.zettee.syndicatebot.audio.BotAudioSendHandler;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuildMusicManager {

    public final AudioPlayer player;
    public final BotAudioScheduler scheduler;

    @Getter private HashMap<Guild, HashMap<AudioTrack, Member>> requests = new HashMap<>();

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        this.player = manager.createPlayer();
        this.scheduler = new BotAudioScheduler(player, guild);
        player.addListener(this.scheduler);
    }

    public void addRequest(AudioTrack track, Member member){
        HashMap<AudioTrack, Member> reqs = this.requests.getOrDefault(member.getGuild(), new HashMap<>());
        System.out.println(reqs);
        reqs.put(track, member);

        this.requests.put(member.getGuild(), reqs);
    }

    public AudioSendHandler getSendHandler(){
        return new BotAudioSendHandler(player);
    }
}
