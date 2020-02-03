package de.zettee.syndicatebot.command.commands.connection;

import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
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

        message.getGuild().getAudioManager().closeAudioConnection();
        Messages.sendText(":wave: Stimmenkanal verlassen.", message.getTextChannel());
    }
}
