package com.example.fashionshopapp.model;

public class AiChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT =1;

    private final String message;
    private final int messageType;

    public AiChatMessage(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() { return message; }
    public int getMessageType() { return messageType; }
}
