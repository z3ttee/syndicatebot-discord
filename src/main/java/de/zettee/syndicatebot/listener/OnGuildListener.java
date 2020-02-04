package de.zettee.syndicatebot.listener;

import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.configuration.Configurator;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnGuildListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(!event.getAuthor().isBot()){
            if(event.getMessage().getContentRaw().startsWith(Configurator.ofGuild(event.getGuild()).getPrefix())) {
                CommandHandler.getInstance().handleCommand(event.getMessage());
            }
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Configurator.ofGuild(event.getGuild()).create();
    }
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        Configurator.ofGuild(event.getGuild()).delete();
    }
    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        Configurator.ofGuild(event.getGuild()).delete();
    }
    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Configurator.ofGuild(event.getGuild());
    }
}
