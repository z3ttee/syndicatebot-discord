package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.validator.routines.UrlValidator;

public class CMD_Play extends Command {

    public CMD_Play() {
        super("play", "<url>", "Play a song by url", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(args.length < 1) {
            Messages.sendUsage(this, message.getTextChannel());
            return;
        }

        String param = args[0];
        if(UrlValidator.getInstance().isValid(param)) {
            // Youtube link or stream
        } else {
            // TODO: Search on youtube
        }
    }
}
