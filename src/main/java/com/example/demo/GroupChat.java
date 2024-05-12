package com.example.demo;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupChat implements Serializable {
    String groupName;
    ArrayList<String> admins;
    ArrayList<String> members;
    ArrayList<Message> messages;

    public GroupChat(String name) {
        groupName = name;
        admins = new ArrayList<String>();
        members = new ArrayList<String>();
        messages = new ArrayList<Message>();
    }
    public GroupChat(){
    }
    public boolean isMember(String name){
        return members.contains(name);
    }

    public boolean isAdmin(String name){
        return admins.contains(name);
    }

    public int noOfAdmins(){
        return  admins.size();
    }

    public void removeUser(String name){
        admins.remove(name);
        members.remove(name);
    }

    public boolean joinGroup(String username){
//        if(name.equals(entranceCode)){
//            members.add(username);
//            return true;
//        }
//        else{
//            System.out.println( "Entrance code didn't match");
//            return false;
//        }

        if(!members.contains(username)){
            members.add(username);
            return true;
        }
        else{
            return false;
        }
    }

}

