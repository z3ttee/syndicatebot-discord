package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Stop extends Command {
    public CMD_Stop() {
        super("stop", "", "Stops the music", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        GuildMusicManager musicManager = BotConnection.getGuildMusicManager(message.getGuild());

        if(musicManager.player.getPlayingTrack() == null) {
            Messages.sendError("Vom Volksempfänger wird gerade keine Sendung ausgestrahlt.", message.getTextChannel());
            return;
        }

        BotConnection.stopAudio(message.getGuild());
        Messages.sendText(":stop_button: Die derzeitige Sendung wurde gestoppt und die Warteschlange geleert. :dash:", message.getTextChannel());
    }
}
