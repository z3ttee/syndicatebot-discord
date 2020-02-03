package de.zettee.syndicatebot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class BotAudioSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public BotAudioSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        return lastFrame != null;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        if(data != null) {
            return ByteBuffer.wrap(data);
        } else {
            return null;
        }
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
