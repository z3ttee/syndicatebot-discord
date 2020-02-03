package de.zettee.syndicatebot.command;

import de.zettee.syndicatebot.configuration.Configurator;
import de.zettee.syndicatebot.messages.Messages;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommandHandler {

    private static CommandHandler instance;

    @Getter private HashMap<String, Command> commands = new HashMap<>();

    public static CommandHandler getInstance() {
        if(instance == null) instance = new CommandHandler();
        return instance;
    }

    public void registerCommand(Command command){
        commands.put(command.getName(), command);
    }
    public void handleCommand(Message message){
        String msg = message.getContentRaw().replace(Configurator.ofGuild(message.getGuild()).getPrefix(), "");

        String[] args = msg.split(" ");
        String name = args[0];

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.remove(0);

        Command command = commands.get(name);

        if(command != null) {
            command.execute(message, arguments.toArray(new String[0]));
        } else {
            Messages.sendError(":mag: Befehl nicht gefunden.", message.getTextChannel());
        }
    }
    public void perform(Message message, Command command, String[] args) {
        command.execute(message, args);
    }
}
