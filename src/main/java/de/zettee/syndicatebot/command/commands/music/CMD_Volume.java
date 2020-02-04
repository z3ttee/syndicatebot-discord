package de.zettee.syndicatebot.command.commands.music;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.audio.manager.GuildMusicManager;
import de.zettee.syndicatebot.command.Category;
import de.zettee.syndicatebot.command.Command;
import de.zettee.syndicatebot.messages.Messages;
import net.dv8tion.jda.api.entities.Message;

public class CMD_Volume extends Command {
    public CMD_Volume() {
        super("volume", "<0-100>", "Change volume of music", Category.MUSIC);
    }

    @Override
    public void execute(Message message, String[] args) {
        GuildMusicManager musicManager = BotConnection.getGuildMusicManager(message.getGuild());

        if(!message.getGuild().getAudioManager().isConnected() || musicManager.player.getPlayingTrack() == null) {
            Messages.sendError("Derzeit erfolgt keine Sendung.", message.getTextChannel());
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex){
            Messages.sendError("Überprüfe das Format der Zahl auf Fehler.", message.getTextChannel());
            return;
        }

        musicManager.scheduler.setVolume(amount);
        message.getGuild().getAudioManager().setSelfMuted((amount == 0));
        Messages.sendText((amount >= 66 ? ":loud_sound:" : amount >= 33 ? ":sound:" : amount > 0 ? ":speaker:" : ":mute:")+" Lautstärke wurde auf ` "+amount+" ` gestellt.", message.getTextChannel());
    }
}
