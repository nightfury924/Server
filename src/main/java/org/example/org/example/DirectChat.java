package org.example;

import java.io.Serializable;
import java.util.ArrayList;

public class DirectChat implements Serializable{
    String[] participants = new String[2];
    ArrayList<Message> messages = new ArrayList<>();
    public DirectChat(String p1, String p2, Message initialMessage){
        participants[0] = p1;
        participants[1] = p2;
        messages.add(initialMessage);
    }
    public void displayChat(){
        int i = 1;
        for (Message message : messages) {
            System.out.println(" "+i+":"+message.sender+" : "+message.text);
            i++;
        }
    }
}
