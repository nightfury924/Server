package com.example.demo;

import java.io.Serializable;
import java.util.ArrayList;

public class Account extends PersonelData implements Serializable {
    String email;
    String password;
    ArrayList<DirectChat>  direct_chats = new ArrayList<>();
    ArrayList<GroupChat> group_chats = new ArrayList<>();
    Message tempMsg;
    public Account(String password,String userName,String dateOfBirth,String email){
        super(userName,dateOfBirth);
        this.password=password;
        this.email = email;
    }
    public Account(){
        super();
    }
    public void createDirectChat(String p1, String p2,Message msg) {
        DirectChat dc = new DirectChat(p1,p2,msg);
        direct_chats.add(dc);
    }
    public void addGroupChat(GroupChat groupChat){
        group_chats.add(groupChat);
    }
}
