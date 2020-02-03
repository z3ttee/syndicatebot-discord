package de.zettee.syndicatebot;

import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.command.commands.connection.CMD_Join;
import de.zettee.syndicatebot.command.commands.connection.CMD_Leave;
import de.zettee.syndicatebot.command.commands.general.CMD_Help;
import de.zettee.syndicatebot.command.commands.CMD_Update;
import de.zettee.syndicatebot.command.commands.music.CMD_Play;
import de.zettee.syndicatebot.configuration.Configurator;
import de.zettee.syndicatebot.listener.OnGuildListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Core {

    @Getter private static Core instance;

    public Core(String token) {
        BotConnection.initialize();
        Configurator.createInstance();
        CommandHandler.getInstance();

        // GENERAL
        CommandHandler.getInstance().registerCommand(new CMD_Help());
        CommandHandler.getInstance().registerCommand(new CMD_Update());

        // CONNECTION
        CommandHandler.getInstance().registerCommand(new CMD_Join());
        CommandHandler.getInstance().registerCommand(new CMD_Leave());

        // Music
        CommandHandler.getInstance().registerCommand(new CMD_Play());

        JDABuilder builder = new JDABuilder();
        builder.setToken(token);
        builder.setAutoReconnect(true);
        builder.setActivity(Activity.listening("ss help"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(new OnGuildListener());

        try {
            builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void main(@NotNull String[] args) {
        new Core(args[0]);
    }

}
