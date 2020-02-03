package de.zettee.syndicatebot.audio;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class BotConnection {

    public static void createNew(VoiceChannel voiceChannel) {
        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSendingHandler(new BotAudioSendHandler(null));
    }

}
