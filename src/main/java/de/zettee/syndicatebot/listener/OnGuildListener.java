package de.zettee.syndicatebot.listener;

import de.zettee.syndicatebot.command.CommandHandler;
import de.zettee.syndicatebot.configuration.Configurator;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        ExecutorService service = Executors.newFixedThreadPool(4);
        service.execute(() -> {
                event.getGuild().getMembers().forEach(member -> {
                    if(member.getUser().isBot()) return;

                    List<Role> roles = member.getRoles();
                    if (!roles.containsAll(event.getGuild().getRoles())) {
                        event.getGuild().getRoles().forEach(role -> {
                            if(role.isManaged() && !role.isPublicRole()) return;

                            if (!roles.contains(role)) {
                                try {
                                    event.getGuild().addRoleToMember(member, role).complete();
                                } catch (HierarchyException | ErrorResponseException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        });
                    }
                });

        });
    }
}
