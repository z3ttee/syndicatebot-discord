package de.zettee.syndicatebot.command.commands.connection;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Join extends Command {

    public CMD_Join() {
        super("join", "", "Connect the bot to your channel", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(message.getMember() == null) return;

        if(message.getMember().getVoiceState() == null) {
            Messages.sendError("Du musst mit einem Stimmenkanal verbunden sein.", message.getTextChannel());
            return;
        }

        if(message.getMember().getVoiceState().getChannel() == null) {
            Messages.sendError("Mit diesem Stimmenkanal kann keine Verbindung hergestellt werden.", message.getTextChannel());
            return;
        }

        if(message.getGuild().getAudioManager().isConnected()) {
            Messages.sendError("Ich bin bereits mit einem Stimmenkanal verbunden.", message.getTextChannel());
            return;
        }

        BotConnection.createNew(message.getMember().getVoiceState().getChannel(), message.getTextChannel());
    }
}
