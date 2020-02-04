package de.zettee.syndicatebot.messages;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.configuration.Configurator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;

public class Messages {

    public static void sendText(String message, TextChannel channel) {
        channel.sendMessage("**"+message+"**").queue();
    }
    public static void sendError(String message, TextChannel channel){
        channel.sendMessage(":no_entry: **"+message+"**").queue();
    }
    public static void sendUsage(Command command, TextChannel channel) {
        channel.sendMessage(":question: **Meintest du** ` "+ Configurator.ofGuild(channel.getGuild()).getPrefix()+command.getName()+" "+command.getUsage()+" ` ?").queue();
    }

    public static void sendPlayerInfo(TextChannel channel, boolean deleteOld){
        AudioTrack track = BotConnection.getGuildMusicManager(channel.getGuild()).player.getPlayingTrack();

        Duration duration = Duration.ofMillis(track.getDuration());
        long min = duration.toMinutes();
        long sec = duration.minusMinutes(min).getSeconds();

        String m = String.valueOf(min);
        String s = String.valueOf(sec);

        if(m.length() <= 1) m = "0"+m;
        if(s.length() <= 1) s = "0"+s;

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
                                .addField("Laufzeit",m+":"+s, false)
                                .setFooter("Hinzugefügt von "+member.getUser().getName()+"#"+member.getUser().getDiscriminator(), member.getUser().getAvatarUrl()).build())
                        .build()
        ).queue(success -> {
            if(deleteOld) {
                musicManager.scheduler.setCurrentlyPlayingInfo(success);
            }
        });
    }

    public static void sendEnqueuedInfo(TextChannel channel, AudioTrack track) {
        AudioPlayer player = BotConnection.getGuildMusicManager(channel.getGuild()).player;
        BlockingQueue<AudioTrack> queue = BotConnection.getGuildMusicManager(channel.getGuild()).scheduler.getQueue();

        long timeMillis = 0;

        for(AudioTrack t : queue) {
            timeMillis += t.getDuration();
        }

        timeMillis += (player.getPlayingTrack().getDuration()-player.getPlayingTrack().getPosition());

        Duration duration = Duration.ofMillis(timeMillis);
        long min = duration.toMinutes();
        long sec = duration.minusMinutes(min).getSeconds();

        String m = String.valueOf(min);
        String s = String.valueOf(sec);

        if(m.length() <= 1) m = "0"+m;
        if(s.length() <= 1) s = "0"+s;

        channel.sendMessage(
                new MessageBuilder(":white_check_mark: :clipboard: **Sendung zur Warteschlange hinzugefügt**")
                        .setEmbed(new EmbedBuilder()
                                .setTitle(track.getInfo().title,track.getInfo().uri)
                                .setDescription(" ")
                                .addField("# in Warteschlange", String.valueOf(queue.size()+1), false)
                                .addField("sendet in ",m+":"+s, false).build())
                        .build()
        ).queue();
    }

    public static void sendTextAndGet(String message, TextChannel channel, MessageSentEvent event) {
        channel.sendMessage("**"+message+"**").queue(event::messageSent);
    }

}
