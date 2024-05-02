package com.example.demo;

import java.util.ArrayList;

public class DirectChat {
    String[] participants = new String[2];
    ArrayList<Message> messages = new ArrayList<>();
    public DirectChat(String p1, String p2, Message initialMsg){
        participants[0] = p1;
        participants[1] = p2;
        messages.add(initialMsg);
    }
    public void displayChat(){
        int i = 1;
        for (Message message : messages) {
            System.out.println(" "+i+":"+message.sender+" : "+message.text);
            i++;
        }
    }

//    public void addMessage(String sender, String text){
//
//    }
}

