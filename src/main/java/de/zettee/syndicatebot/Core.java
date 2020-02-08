package de.zettee.syndicatebot;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import de.zettee.syndicatebot.audio.BotConnection;
import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.command.commands.connection.CMD_Join;
import de.zettee.syndicatebot.command.commands.connection.CMD_Leave;
import de.zettee.syndicatebot.command.commands.general.CMD_Help;
import de.zettee.syndicatebot.command.commands.CMD_Update;
import de.zettee.syndicatebot.command.commands.music.*;
import de.zettee.syndicatebot.configuration.Configurator;
import de.zettee.syndicatebot.listener.OnGuildListener;
import de.zettee.syndicatebot.listener.OnReactionListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class Core {

    @Getter private static Core instance;

    @Getter private final String YOUTUBE_API_KEY;
    @Getter private final String APPLICATION_NAME = "SyndicateBot Discord";
    @Getter private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public Core(String token, String api_key) {
        System.out.println(api_key);

        YOUTUBE_API_KEY = api_key;
        instance = this;

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
        CommandHandler.getInstance().registerCommand(new CMD_Pause());
        CommandHandler.getInstance().registerCommand(new CMD_Loop());
        CommandHandler.getInstance().registerCommand(new CMD_Volume());
        CommandHandler.getInstance().registerCommand(new CMD_Skip());
        CommandHandler.getInstance().registerCommand(new CMD_Stop());

        JDABuilder builder = new JDABuilder();
        builder.setToken(token);
        builder.setAutoReconnect(true);
        builder.setActivity(Activity.listening("ss help"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(
                new OnGuildListener(),
                new OnReactionListener());

        try {
            builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void main(@NotNull String[] args) {
        new Core(args[0], args[1]);
    }

    public YouTube getYoutubeService() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME).build();
    }

    public LinkedBlockingQueue<String> performYoutubeSearch(String args, long maxResults) throws GeneralSecurityException, IOException {
        // TODO: Save etag and query to reduce api calls
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    YouTube service = getYoutubeService();
                    YouTube.Search.List request = service.search().list("snippet").set("q", args).setKey(YOUTUBE_API_KEY);
                    SearchListResponse response = request.setMaxResults(maxResults).execute();
                    SearchResult result = response.getItems().get(0);

                    String playlistID = result.getId().getPlaylistId();
                    String videoID = result.getId().getVideoId();
                    String playableUrl = "https://www.youtube.com/watch?v=" + videoID;

                    if (playlistID != null) {
                        playableUrl += "&list=PLK4IGlmN6libzkoH_QlNW0dK2q2YQF4Yl";
                    }

                    queue.offer(playableUrl);
                } catch (Exception ex){
                    queue.offer("timed out");
                    ex.printStackTrace();
                }
            }
        }, 0L);

        return queue;
    }
}
