package com.example.demo;

import java.io.*;
import java.net.*;
// import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import javafx.application.Application;


//change:
//deleteMessage() in MainController
//deleteDirectMessage() in client
//deleteDirectMessage() in clienthandler
//changed account name and also in personel data
//deleteGroupMessage()
//karna hai
//clienthandler mai isValidFOrgroupcCreation
//

public class Client{
    private Socket socket;
    public BufferedReader in;
    public BufferedWriter out;
    public Account userAccount;
    public static Gson  gson = new Gson();
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 27508;
    static Scanner sc = new Scanner(System.in);
    static int vGroupNameVar = 0;
    static int vDirectNameVar = 0;
    static public int vboxMessageUpdate = 0;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            // this.username = username;
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (Exception e) {
            System.out.println("a");
            e.printStackTrace();
        }
    }


    public void startListening(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                String input;
                Message msgIncoming;
                while(socket.isConnected()){
                    try {
                        input = in.readLine();
                        if(input.equals("update Group Chat")){
                            updateGroupChat(in.readLine());
                            input = "";
                        }else if(input.equals("update direct chat")){
                            updateDirectChat(in.readLine());
                            input = "";
                        }else if(input.equals("add a new direct chat")){
                            DirectChat dc = gson.fromJson(in.readLine(), DirectChat.class);
                            userAccount.direct_chats.add(dc);
                            input = "";
                        }else if(input.equals("validate group name for group creation")){
                            input = in.readLine();
                            if(input.equals("true")) {
                                Client.vGroupNameVar = 1;
                            }else{
                                Client.vGroupNameVar = 2;
                            }
                        }else if(input.equals("validate direct name for direct creation")){
                            input = in.readLine();
                            if(input.equals("true")) {
                                Client.vDirectNameVar = 1;
                            }else{
                                Client.vDirectNameVar = 2;
                            }
                        }else{
                            msgIncoming = gson.fromJson(input, Message.class);
                            System.out.println(msgIncoming.sender + " : "+ msgIncoming.text);
                            addMessage(msgIncoming);
                        }

                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    public void updateGroupChat(String input){
        GroupChat  updateGc = gson.fromJson(input,GroupChat.class);
        for (GroupChat group : userAccount.group_chats) {
            if(updateGc.groupName.equals(group.groupName)){
                userAccount.group_chats.set(userAccount.group_chats.indexOf(group), updateGc);
                break;
            }
        }
    }

    public void updateDirectChat(String input){
        DirectChat updateDc = gson.fromJson(input, DirectChat.class);
        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(updateDc.participants[0]) && dc.participants[1].equals(updateDc.participants[1])){
                userAccount.direct_chats.set(userAccount.direct_chats.indexOf(dc),updateDc);
                if(vboxMessageUpdate == 0){
                    vboxMessageUpdate = 1;
                }else{
                    vboxMessageUpdate = 0;
                }
                break;
            }
        }
    }

    public void joinGroup() throws Exception{
        System.out.println(" Enter Group Entrance Code: ");
        String code = Client.sc.nextLine();
        out.write("entering a server");
        out.newLine();
        out.flush();
        out.write(code);
        out.newLine();
        out.flush();
        String serverReply = in.readLine();
        if(serverReply.equals("group joined successfully")){
            GroupChat gp = gson.fromJson(in.readLine(), GroupChat.class);
            userAccount.group_chats.add(gp);
            System.out.println(" Joined Successfully!");
        }
        else if(serverReply.equals("no matching group")){
            System.out.println(" No Matching Group Found. Verify your entrance code with Group admins.");
        }
    }

    public void makeAdmin(GroupChat gp)  throws Exception {
//        gp.displayMembers();
        int user;
        System.out.print(" Select User to Upgrade to Admin : ");
        user = Client.sc.nextInt();
        Client.sc.nextLine();
        gp.admins.add(gp.members.get(user-1));
        out.write("Adding an admin");
        out.newLine();
        out.flush();
        out.write(gp.groupName);
        out.newLine();
        out.flush();
        out.write(gp.members.get(user-1));
        out.newLine();
        out.flush();
    }

    public void leaveGroup(GroupChat gp) throws Exception{
        gp.removeUser(userAccount.getUsername());
        out.write("leaving Group");
        out.newLine();
        out.flush();
        out.write(gp.groupName);
        out.newLine();
        out.flush();
        out.write(userAccount.getUsername());
        out.newLine();
        out.flush();
    }

    public void kickMember(GroupChat gp) throws Exception{
        int i = 1;
        for (String name : gp.members) {
            System.out.println(" "+i+" :"+name);
            i++;
        }
        int user;
        System.out.print(" Select User to Kick : ");
        user = Client.sc.nextInt();
        Client.sc.nextLine();
        gp.removeUser(gp.members.get(user-1));
        out.write("leaving Server");
        out.newLine();
        out.flush();
        out.write(gp.groupName+" "+gp.members.get(user-1));
        out.newLine();
        out.flush();
    }

    public void deleteGroupMessage(String groupName, int index) throws Exception{
        for (GroupChat gc : this.userAccount.group_chats) {
            if(gc.groupName.equals(groupName)){
                if(gc.isAdmin(this.userAccount.getUsername()) || gc.messages.get(index).sender.equals(this.userAccount.getUsername()) ){
                    gc.messages.remove(index);
                    out.write("group message deleted");
                    out.newLine();
                    out.flush();
                    out.write(groupName);
                    out.newLine();
                    out.flush();
                    out.write(index);
                    out.flush();
                    break;
                }
            }
        }
    }

    public void deleteDirectMessage(String receiver,int index)throws Exception{          // have not implemented out of bound error checking for message deletion as we are going to select message using ui


        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(receiver) || dc.participants[1].equals(receiver)){
                if(dc.messages.get(index).sender.equals(this.userAccount.getUsername())){
                    dc.messages.remove(index);
                    out.write("direct message deleted");
                    out.newLine();
                    out.flush();
                    out.write(receiver);
                    out.newLine();
                    out.flush();
                    out.write(index);
                    out.flush();
                    break;
                }

            }
        }

    }

    public void editGroupMessage(String groupName,int index, String newMessage) throws Exception{
        for (GroupChat gc : userAccount.group_chats) {
            if(gc.groupName.equals(groupName)){
                gc.messages.get(index).setText(newMessage);
                out.write("group message edited");
                out.newLine();
                out.flush();
                out.write(groupName);
                out.newLine();
                out.flush();
                out.write(index);
                out.flush();
                out.write(newMessage);
                out.newLine();
                out.flush();
                break;
            }
        }
    }

    public void editMessage(String reciever, int index, String newMsg) throws Exception{
        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(reciever) || dc.participants[1].equals(reciever)){
                Message msg= dc.messages.get(index);

                if(msg.sender.equals(this.userAccount.getUsername())){    // edit from both users chat list
                    out.write("direct message edited");
                    out.newLine();
                    out.flush();
                    out.write(gson.toJson(msg));
                    out.newLine();
                    out.flush();
                    out.write(index);
                    out.flush();
                    out.write(newMsg);
                    out.newLine();
                    out.flush();
                    dc.messages.get(index).text = newMsg;
                }
                else{
                    System.out.println(" You can only edit your own messages.");
                }
            }
            break;
        }
    }

    public void startNewDirectChat(String username) throws IOException{
        out.write("start a new direct chat\n");
        out.flush();
        out.write(username);
        out.newLine();
        out.flush();
    }

    public void startNewGroupChat(String groupName) throws IOException{

        GroupChat gp = new GroupChat(groupName);
        gp.admins.add(userAccount.getUsername());
        gp.members.add(userAccount.getUsername());
        userAccount.group_chats.add(gp);
        out.write("new Group Chat\n");
        out.flush();
        out.write(gson.toJson(gp));
        out.newLine();
        out.flush();
    }

    public void sendDirectMessage(String message, String recipient) throws IOException{
        userAccount.tempMsg.text = message;
        userAccount.tempMsg.sender = userAccount.getUsername();
        userAccount.tempMsg.receiver = recipient;
        userAccount.tempMsg.chatType = true;
        if (!addMessage(userAccount.tempMsg)){
            return ;
        }
        String outgoing = gson.toJson(userAccount.tempMsg);
        userAccount.tempMsg = new Message();
        out.write(outgoing);
        out.newLine();
        out.flush();
        // userAccount.tempMsg.clear();
    }

    public void sendGroupMessage(String text, String recipient) throws IOException{
        userAccount.tempMsg.text = text;
        userAccount.tempMsg.sender = userAccount.getUsername();
        userAccount.tempMsg.receiver = recipient;
        userAccount.tempMsg.chatType = false;
        addMessage(userAccount.tempMsg);
        String outgoing = gson.toJson(userAccount.tempMsg);
        userAccount.tempMsg = new Message();
        out.write(outgoing);
        out.newLine();
        out.flush();
        //userAccount.tempMsg.clear();
    }

    public boolean addMessage(Message incMsg){
        if(incMsg.chatType){
            for (DirectChat dc : userAccount.direct_chats) {
                if(dc.participants[0].equals(incMsg.receiver) || dc.participants[1].equals(incMsg.receiver)){
                    dc.messages.add(incMsg);
                    System.out.println("message added");
                    return true;
                }
            }
            System.out.println(" First start a new Direct chat with this Recipient");
            return false;
        }
        else{
            for (GroupChat gc : userAccount.group_chats){
                if(gc.groupName.equals(incMsg.receiver)){
                    gc.messages.add(incMsg);
                }
            }
            return true;
        }
    }

    public void validateGroupName(String groupName) throws IOException {
        out.write("validate group name for group creation\n");
        out.flush();
    }


    //authorization has problems if rejected program just hangs
//    public boolean login() throws IOException{
//        out.write("login\n");
//        out.flush();
//        for (int i = 1; i <= 3; i++) {
//            System.out.println(" Enter UserName: ");
//            String temp_userName = Client.sc.nextLine();
//            System.out.println(" Password   : ");
//            String temp_password = Client.sc.nextLine();
//            out.write(temp_userName);
//            out.newLine();
//            out.flush();
//            out.write(temp_password);
//            out.newLine();
//            out.flush();
//            String response = in.readLine();
//            if(response.equals("true")){
//                receiveAccount();
//                return true;
//            } else{
//                System.out.println(" Authentication Failed Try AGain. Attempts Remaining "+ (3-i));
//            }
//        }
//        socket.close();
//        return false;
//    }





    public void receiveAccount( )throws IOException{
        String jsonAccount = in.readLine();
        userAccount = gson.fromJson(jsonAccount, Account.class);
        userAccount.tempMsg = new Message();
        System.out.println("User Name :"+userAccount.getUsername());
        receiveGroupChats();
    }

    public void receiveGroupChats() throws IOException{
        String incoming = in.readLine();
        while (!incoming.equals("Finished Sending GroupChats")) {
            GroupChat tmpGroupChat = gson.fromJson(incoming, GroupChat.class);
            userAccount.addGroupChat(tmpGroupChat);
            incoming = in.readLine();
        }
    }


    public void isValid(String username) throws IOException{
        out.write("check validity for new direct chat\n");
        out.flush();
        out.write(username);
        out.newLine();
        out.flush();
    }

    public static void main(String[] args) throws UnknownHostException, IOException,Exception {
        Application.launch(ClientApplication.class, args);
    }

}