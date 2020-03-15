package de.zettee.syndicatebot.command.commands.admin;

import de.zettee.syndicatebot.Core;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Devmode extends Command {

    public CMD_Devmode() {
        super("devmode", "", "", Category.HIDDEN);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(Core.isDevmode()) {
            Core.disableDevmode();
            Messages.sendText(":negative_squared_cross_mark: Devmode deaktiviert.", message.getTextChannel());
        } else {
            Core.enableDevmode();
            Messages.sendText(":white_check_mark: Devmode aktiviert.", message.getTextChannel());
        }
    }
}
