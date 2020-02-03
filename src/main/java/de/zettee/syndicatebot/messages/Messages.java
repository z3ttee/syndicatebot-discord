package de.zettee.syndicatebot.messages;

import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.configuration.Configurator;
import net.dv8tion.jda.api.entities.TextChannel;

public class Messages {

    public static void sendText(String message, TextChannel channel) {
        channel.sendMessage(message).queue();
    }
    public static void sendError(String message, TextChannel channel){
        channel.sendMessage(":no_entry: "+message).queue();
    }
    public static void sendUsage(Command command, TextChannel channel) {
        channel.sendMessage(":question: Meintest du ` "+ Configurator.ofGuild(channel.getGuild()).getPrefix()+command.getName()+" "+command.getUsage()+" ` ?").queue();
    }

}
