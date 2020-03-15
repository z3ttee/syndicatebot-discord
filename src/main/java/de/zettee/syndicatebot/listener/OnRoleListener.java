package de.zettee.syndicatebot.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

// NOTE: For trolling purposes only
public class OnRoleListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
        super.onGuildMemberRoleRemove(event);
        event.getRoles().forEach(role -> event.getGuild().addRoleToMember(event.getMember(), role).queue());
    }
}
