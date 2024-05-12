package com.example.demo;

import java.io.Serializable;
import java.sql.Time;

public class Message implements Serializable {
    Time time;
    String text;
    boolean chatType; // true for directChat and false for groupchat
    String sender;
    String receiver;

    public Message(String receiver, String sender, boolean chatType, String text) {
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
        this.chatType = chatType;
        this.time = new Time(System.currentTimeMillis());

    }
    public Message(){
        time = null;
        text = "";
        chatType = false;
        sender = "";
        receiver = "";
    }
    public void clear() {
        this.time = null;
        this.text = "";
        this.chatType = false;
        this.sender = "";
        this.receiver = "";
    }

    public void setText(String text) {
        this.text = text;
    }

}


