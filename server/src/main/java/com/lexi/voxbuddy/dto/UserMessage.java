package com.lexi.voxbuddy.dto;

import lombok.Getter;

@Getter
public class UserMessage {

    private final MessageType type = MessageType.USER_MESSAGE;

    private String text;

}