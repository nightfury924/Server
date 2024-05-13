package com.example.demo;

import java.io.*;
import java.util.ArrayList;

public class GroupChatStorage {
    public static void main(String[] args) throws IOException {
//        ArrayList<GroupChat> allGroupChats = new ArrayList<>();
//        GroupChat gc1 = new GroupChat("pingpong");
//        GroupChat gc2 = new GroupChat("pingpong31");
//        GroupChat gc3 = new GroupChat("ping123pong");
//
//        gc3.joinGroup("cpoing123");
//        gc3.joinGroup("johndoe123");
//        gc3.joinGroup("abced123");
//
//        Message gcm1 = new Message("ping123 pong", "cpoing123", false, "hi");
//        Message gcm2 = new Message("ping123 pong", "johndoe123", false, "hello");
//        Message gcm3 = new Message("ping123 pong", "abced123", false, "hallo");
//
//        gc3.messages.add(gcm1);
//        gc3.messages.add(gcm2);
//        gc3.messages.add(gcm3);
//
//        allGroupChats.add(gc1);
//        allGroupChats.add(gc2);
//        allGroupChats.add(gc3);
//        for(GroupChat gc : allGroupChats){
//            storeGroupChat(gc);
//        }
        Account account = new Account("password123", "johndoe123", "1990-01-01", "johndoe123@example.com");
        Account account2 = new Account("password123", "abced123", "1990-01-01", "abc123@example.com");
        Account account1 = new Account("password123", "cpoing123", "12-1-2022", "poingkiport@gmail.com");
        Message m1 = new Message("johndoe123", "cpoing123", true, "hello");
        Message m2 = new Message("johndoe123", "cpoing123", true, "what r u doing");
        Message m3 = new Message("cpoing123", "johndoe123", true, "how are you");
        Message m4 = new Message("cpoing123", "johndoe123", true, "welcome to app");
        Message m5 = new Message("cpoing123", "johndoe123", true, "123456");
        Message m6 = new Message("johndoe123", "cpoing123", true, "pooooooooooooooooool");
        Message m7 = new Message("cpoing123", "johndoe123", true, "nice to meet you");
        Message m8 = new Message("abced123", "cpoing123", true, "i am good");
        Message m9 = new Message("cpoing123", "abced123", true, "johohohohohoho");


        DirectChat dc1 = new DirectChat("johndoe123", "cpoing123",  new Message("johndoe123", "cpoing123", true, "a"));
        DirectChat dc2 = new DirectChat("abced123", "cpoing123", new Message("abced123", "cpoing123", true, "a"));

        dc1.messages.add(m1);
        dc1.messages.add(m2);
        dc1.messages.add(m3);
        dc1.messages.add(m4);
        dc1.messages.add(m5);
        dc1.messages.add(m6);
        dc1.messages.add(m7);

        dc2.messages.add(m8);
        dc2.messages.add(m9);


        account.direct_chats.add(dc1);
        account1.direct_chats.add(dc1);
        account2.direct_chats.add(dc2);
        account1.direct_chats.add(dc2);
        ArrayList<Account> allRegisteredAccounts = new ArrayList<>();
        allRegisteredAccounts.add(account);
        allRegisteredAccounts.add(account1);
        allRegisteredAccounts.add(account2);
        for(Account gc : allRegisteredAccounts){
            storeAccount(gc);
        }


    }
    static public void storeAccount(Account account) throws IOException{
        ObjectOutputStream objectWriter = new ObjectOutputStream(new FileOutputStream("Accounts/"+account.getUsername()+".bin"));
        objectWriter.writeObject(account);
        objectWriter.flush();
        objectWriter.close();
    }
    static private void storeGroupChat(GroupChat groupChat) throws IOException {
        ObjectOutputStream objectWriter = new ObjectOutputStream(new FileOutputStream("GroupChats/"+groupChat.groupName+".bin"));
        objectWriter.writeObject(groupChat);
        objectWriter.flush();
        objectWriter.close();
    }
}
