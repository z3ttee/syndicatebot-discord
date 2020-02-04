package de.zettee.syndicatebot.command.commands.general;

import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Help extends Command {

    public CMD_Help() {
        super("help", "", "Get a list of all commands", Category.GENERAL);
    }

    @Override
    public void execute(Message message, String[] args) {



    }
}
