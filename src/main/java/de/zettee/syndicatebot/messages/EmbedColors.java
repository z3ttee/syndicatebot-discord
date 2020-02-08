package de.zettee.syndicatebot.messages;

import lombok.Getter;

import java.awt.*;

public enum EmbedColors {

    ERROR(new Color(255,148,148));

    @Getter private final Color color;
    EmbedColors(Color color) {
        this.color = color;
    }
}
