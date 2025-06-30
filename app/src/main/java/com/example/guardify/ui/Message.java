package com.example.guardify.ui;

import com.google.firebase.Timestamp;

public class Message {
    private String senderId;
    private String text;
    private Object timestamp;  // Accept both Long and Timestamp
    private String status;
    private String senderName;
    public Message() { }

    public Message(String senderId,String senderName, String text, Timestamp timestamp, String status) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.senderName=senderName;
        this.status = status;
    }
    public String getSenderName() { return senderName; }
    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public String getStatus() { return status; }

    public Timestamp getTimestamp() {
        if (timestamp instanceof Timestamp) {
            return (Timestamp) timestamp;
        } else if (timestamp instanceof Long) {
            return new Timestamp(new java.util.Date((Long) timestamp));
        } else {
            return null;
        }
    }
}
