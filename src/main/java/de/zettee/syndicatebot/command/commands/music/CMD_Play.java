package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.Core;
import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.command.commands.connection.CMD_Join;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.validator.routines.UrlValidator;

public class CMD_Play extends Command {

    public CMD_Play() {
        super("play", "<url>", "Play a song by url", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        if(args.length == 0) {
            if(!BotConnection.getGuildMusicManager(message.getGuild()).player.isPaused()) {
                Messages.sendUsage(this, message.getTextChannel());
                return;
            }

            CommandHandler.getInstance().perform(message, new CMD_Pause(), new String[0]);
            return;
        }

        if(!message.getGuild().getAudioManager().isConnected()) {
            CommandHandler.getInstance().perform(message, new CMD_Join(), new String[0]);
        }

        String param = args[0];
        if(UrlValidator.getInstance().isValid(param)) {
            // Youtube link or stream
            BotConnection.loadAndPlay(message.getMember(), message.getGuild(), param);
        } else {
            // TODO: Search on youtube
            String query = String.join(" ", args);
            Messages.sendText(":mag::man_detective: Es wird auf YouTube nach ` "+query+" ` gesucht.", message.getTextChannel());

            try {
                String url = Core.getInstance().performYoutubeSearch(query, 1).take();
                BotConnection.loadAndPlay(message.getMember(), message.getGuild(), url);
            } catch (Exception e) {
                Messages.sendError("Die YouTube-Suche wurde durch einen Fehler abgebrochen.", message.getTextChannel());
                e.printStackTrace();
            }
        }
    }
}
