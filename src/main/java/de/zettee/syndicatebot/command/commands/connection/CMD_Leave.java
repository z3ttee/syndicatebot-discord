package de.zettee.syndicatebot.command.commands.connection;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.command.commands.music.CMD_Stop;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Leave extends Command {

    public CMD_Leave() {
        super("leave", "", "", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(!message.getGuild().getAudioManager().isConnected()) {
            Messages.sendError("Ich bin mit keinem Stimmenkanal verbunden.", message.getTextChannel());
            return;
        }

        if(BotConnection.getGuildMusicManager(message.getGuild()).player.getPlayingTrack() != null) {
            CommandHandler.getInstance().perform(message, new CMD_Stop(), new String[0]);
        }

        message.getGuild().getAudioManager().closeAudioConnection();
        Messages.sendText(":wave: Stimmenkanal wurde verlassen.", message.getTextChannel());
    }
}
