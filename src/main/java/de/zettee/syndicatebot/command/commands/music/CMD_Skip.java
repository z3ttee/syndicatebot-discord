package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Skip extends Command {

    public CMD_Skip() {
        super("skip", "(Anzahl)", "Skip some tracks", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(args.length == 0) {
            if(BotConnection.getGuildMusicManager(message.getGuild()).scheduler.getQueue().size() == 0) {
                Messages.sendError("Die Warteschlange ist leider leer.", message.getTextChannel());
                return;
            }

            BotConnection.getGuildMusicManager(message.getGuild()).scheduler.next();
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Messages.sendError("Du hast keine korrekt formatierte Zahl eingegeben.", message.getTextChannel());
            return;
        }


    }
}
