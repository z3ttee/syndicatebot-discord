package de.zettee.syndicatebot.command.commands;

import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Update extends Command {

    public CMD_Update() {
        super("update", "", "", Category.HIDDEN);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(message.getAuthor().getId().equals("230697828073209857")) {

            message.getChannel().sendMessage(":mag: Es wird nach Updates gesucht...").queue((msg -> {

            }));
        }
    }
}
