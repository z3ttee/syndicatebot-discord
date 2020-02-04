package de.zettee.syndicatebot.messages;

import net.dv8tion.jda.api.entities.Message;

public interface MessageSentEvent {
    void messageSent(Message message);
}
