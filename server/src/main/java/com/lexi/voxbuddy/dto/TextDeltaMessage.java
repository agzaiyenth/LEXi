package com.lexi.voxbuddy.dto;


import lombok.Getter;

@Getter
public class TextDeltaMessage {

    private final String id;

    private final MessageType type = MessageType.TEXT_DELTA;

    private final String delta;

    public TextDeltaMessage(String id, String text) {
        this.id = id;
        this.delta = text;
    }

}