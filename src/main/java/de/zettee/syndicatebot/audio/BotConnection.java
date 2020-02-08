package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.messages.EmbedColors;
import de.zettee.syndicatebot.messages.Messages;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.time.Duration;
import java.util.HashMap;

public class BotConnection {

    @Getter private static HashMap<Guild, GuildMusicManager> musicManagers = new HashMap<>();
    @Getter private static HashMap<Guild, TextChannel> boundTextChannels = new HashMap<>();

    @Getter private static AudioPlayerManager playerManager;

    public static void initialize(){
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static void createNew(VoiceChannel voiceChannel, TextChannel textChannel) {
        boundTextChannels.put(textChannel.getGuild(), textChannel);

        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);
    }

    public static void playAudio(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.enqueue(track);
        if(musicManager.player.getPlayingTrack() != null) {
            Messages.sendEnqueuedInfo(BotConnection.getTextChannel(musicManager.getGuild()), track);
        }
    }
    public static void playPlaylist(Member member, GuildMusicManager musicManager, AudioPlaylist playlist) {
        for(AudioTrack track : playlist.getTracks()) {
            musicManager.addRequest(track, member);
            musicManager.scheduler.enqueue(track);
        }
        Messages.sendEnqueuedPlaylistInfo(BotConnection.getTextChannel(musicManager.getGuild()), playlist);
    }

    public static void loadAndPlay(Member member, Guild guild, String url) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.addRequest(track, member);
                playAudio(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playPlaylist(member, musicManager, playlist);
            }

            @Override
            public void noMatches() {
                Messages.sendError(":mag: Keine Treffer für die Suche gefunden.", BotConnection.getTextChannel(guild));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Messages.sendException(BotConnection.getTextChannel(guild), exception);
            }
        });
    }

    public static void stopAudio(Guild guild){
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);
        musicManager.scheduler.getQueue().clear();
    }

    public static GuildMusicManager getGuildMusicManager(Guild guild){
        GuildMusicManager musicManager = musicManagers.get(guild);

        if(musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guild, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }
    public static TextChannel getTextChannel(Guild guild) {
        return boundTextChannels.get(guild);
    }
    public static void setTextChannel(Guild guild, TextChannel channel) {
        boundTextChannels.put(guild, channel);
    }
}
