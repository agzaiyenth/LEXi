package com.lexi.voxbuddy.dto;

public class UserMessage {

    private final MessageType type = MessageType.USER_MESSAGE;

    private String text;

    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}