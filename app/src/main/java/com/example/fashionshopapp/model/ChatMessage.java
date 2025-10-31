// ĐƯỜNG DẪN: com/example/fashionshopapp/model/ChatMessage.java
package com.example.fashionshopapp.model;

import java.util.Date;

public class ChatMessage {
    public String sender_id;
    public String receiver_id;
    public String content;
    public Date created_at;
    public String conversationKey; // Giữ lại trường này

    public ChatMessage() {
    }

    public ChatMessage(String sender_id, String receiver_id, String content, Date created_at, String conversationKey) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
        this.created_at = created_at;
        this.conversationKey = conversationKey;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }
}
    