package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
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
                // TODO
            }

            @Override
            public void noMatches() {
                // TODO: Error message
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                // TODO: Error message
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

    public static void sendPlayerInfo(TextChannel channel, boolean deleteOld){
        AudioTrack track = getGuildMusicManager(channel.getGuild()).player.getPlayingTrack();

        Duration duration = Duration.ofMillis(track.getDuration());
        long min = duration.toMinutes();
        long sec = duration.minusMinutes(min).getSeconds();

        GuildMusicManager musicManager = BotConnection.getGuildMusicManager(channel.getGuild());
        Member member = musicManager.getRequests().get(channel.getGuild()).get(track);


        if(deleteOld && musicManager.scheduler.getCurrentlyPlayingInfo() != null) {
            musicManager.scheduler.getCurrentlyPlayingInfo().delete().queue();
        }

        channel.sendMessage(
                new MessageBuilder(":notes: **Aktuelle Sendung** :notes:")
                        .setEmbed(new EmbedBuilder()
                                .setTitle(track.getInfo().title,track.getInfo().uri)
                                .setDescription(" ")
                                .addField("Sender",track.getInfo().author, false)
                                .addField("Laufzeit",min+":"+sec, false)
                                .setFooter("Hinzugefügt von "+member.getUser().getName()+"#"+member.getUser().getDiscriminator(), member.getUser().getAvatarUrl()).build())
                        .build()
        ).queue(success -> {
            if(deleteOld) {
                musicManager.scheduler.setCurrentlyPlayingInfo(success);
            }
        });
    }
}
