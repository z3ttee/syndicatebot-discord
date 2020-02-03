package de.zettee.syndicatebot.command;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

public abstract class Command {

    @Getter private String name,usage,description;
    @Getter private Category category;

    public Command(String name, String usage, String description, Category category) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.category = category;
    }

    public abstract void execute(Message message, String[] args);
}
