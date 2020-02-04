package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Loop extends Command {
    public CMD_Loop() {
        super("loop", "", "Loop a track", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        GuildMusicManager musicManager = BotConnection.getGuildMusicManager(message.getGuild());

        if(!message.getGuild().getAudioManager().isConnected() || musicManager.player.getPlayingTrack() == null) {
            Messages.sendError("Derzeit erfolgt keine Sendung.", message.getTextChannel());
            return;
        }

        if(musicManager.scheduler.isLoop()) {
            musicManager.scheduler.setLoop(false);
            Messages.sendText(":repeat: Wiederholungsschleife deaktiviert.", message.getTextChannel());
            return;
        }

        musicManager.scheduler.setLoop(true);
        Messages.sendText(":repeat: Wiederholungsschleife aktiviert.", message.getTextChannel());
    }
}
