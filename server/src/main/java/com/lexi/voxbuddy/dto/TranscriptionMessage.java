package com.lexi.voxbuddy.dto;


import lombok.Getter;

@Getter
public class TranscriptionMessage {
    private final String id;

    private final MessageType type = MessageType.TRANSCRIPTION;

    private String text;

    public TranscriptionMessage(String id) {
        this.id = id;
    }

    public TranscriptionMessage setText(String text) {
        this.text = text;
        return this;
    }
}