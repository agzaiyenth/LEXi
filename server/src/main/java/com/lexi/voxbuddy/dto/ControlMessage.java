package com.lexi.voxbuddy.dto;

import lombok.Getter;

@Getter
public class ControlMessage {

    private final MessageType type = MessageType.CONTROL;
    private final String action;

    private String greeting;
    private String id;

    public ControlMessage(String action) {
        this.action = action;
    }

    public ControlMessage setGreeting(String greeting) {
        this.greeting = greeting;
        return this;
    }

    public ControlMessage setId(String id) {
        this.id = id;
        return this;
    }

}