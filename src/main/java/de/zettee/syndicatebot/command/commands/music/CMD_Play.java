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

            // TODO: Async add songs
            try {
                if (param.startsWith("https://open.spotify.com/playlist/")) {
                    /*GetPlaylistsTracksRequest request = Core.getInstance().getSpotifyAPI().getPlaylistsTracks(param.replace("https://open.spotify.com/playlist/", "")).limit(20).build();
                    CompletableFuture<Paging<PlaylistTrack>> pagingFuture = request.executeAsync();

                    // Example Only. Never block in production code.
                    final Paging<PlaylistTrack> playlistTrackPaging = pagingFuture.join();

                    for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                        String query = playlistTrack.getTrack().getName() + " - " + playlistTrack.getTrack().getArtists()[0].getName();
                        System.out.println(query);
                        String url = Core.getInstance().performYoutubeSearch(query, 1).take();
                        BotConnection.loadAndPlay(message, url);
                    }*/
                    Messages.sendError("Funktion in Arbeit", message.getTextChannel());
                    return;
                }
                if (param.startsWith("https://open.spotify.com/track/") || param.startsWith("http://open.spotify.com/track/")) {

                    return;
                }
            } catch (Exception ex){
                ex.printStackTrace();
                return;
            }

            BotConnection.loadAndPlay(message, param);
        } else {
            String query = String.join(" ", args);
            Messages.sendText(":mag::man_detective: Es wird auf YouTube nach ` "+query+" ` gesucht.", message.getTextChannel());

            try {
                String url = Core.getInstance().performYoutubeSearch(query, 1).take();
                BotConnection.loadAndPlay(message, url);
            } catch (Exception e) {
                Messages.sendError("Die YouTube-Suche wurde durch einen Fehler abgebrochen.", message.getTextChannel());
                e.printStackTrace();
            }
        }
    }
}
