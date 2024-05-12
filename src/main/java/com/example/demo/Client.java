package com.example.demo;

import java.io.*;
import java.net.*;
// import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import javafx.application.Application;

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
                        }
                        else{
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
        gp.displayMembers();
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

    public void deleteGroupMessage(GroupChat gp) throws Exception{
        gp.displayChat();
        System.out.println(" Select Message to Delete");
        int msg_index = Client.sc.nextInt();
        Client.sc.nextLine();
        if(gp.messages.get(msg_index-1).sender.equals(userAccount.getUsername()) || gp.admins.contains(userAccount.getUsername())){
            out.write("group message deleted");
            out.newLine();
            out.flush();
            out.write(gp.groupName);
            out.newLine();
            out.flush();
            out.write(gson.toJson(gp.messages.get(msg_index-1)));
            out.newLine();
            out.flush();
            gp.messages.remove(msg_index -1);
        }
        else{
            System.out.println(" You can only delete your own message.");
        }

    }

    public void deleteDirectMessage()throws Exception{          // have not implemented out of bound error checking for message deletion as we are going to select message using ui
        System.out.println(" Select the conversation from which you want to delete:");
        String reciever = Client.sc.nextLine();
        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(reciever) || dc.participants[1].equals(reciever)){
                dc.displayChat();
                System.out.print("Choose Message to Delete:");
                int index = Client.sc.nextInt();
                Client.sc.nextLine();
                Message msg= dc.messages.get(index-1);
                dc.messages.remove(msg);
                out.write("direct message deleted");
                out.newLine();
                out.flush();
                out.write(gson.toJson(msg));
                out.newLine();
                out.flush();
                break;
                /*if(msg.sender.equals(this.userAccount.getUsername())){    // delete from both users chat list
                    dc.messages.remove(msg);
                    out.write("direct message deleted");
                    out.newLine();
                    out.flush();
                    out.write(gson.toJson(msg));
                    out.newLine();
                    out.flush();
                }
                else{                                              // delete for only this user as the message is sent by other user
                    dc.messages.remove(msg);
                }*/
            }
        }

    }

    public void editGroupMessage(String groupName,String message,String newMessage, String sender) throws Exception{
        for (GroupChat gc : userAccount.group_chats) {
            if(gc.groupName.equals(groupName)){
                for (Message msg : gc.messages) {
                    if(msg.sender.equals(sender)){
                        out.write("group message edited");
                        out.newLine();
                        out.flush();
                        out.write(gson.toJson(msg));
                        out.newLine();
                        out.flush();
                        out.write(newMessage);
                        out.newLine();
                        out.flush();
                        msg.text = newMessage;
                        break;
                    }
                }
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

//    public boolean signUp() throws Exception{
//        out.write("signUp\n");
//        out.flush();
//        System.out.print("Enter Name : ");
//        String name = Client.sc.nextLine();
//        String userName;
//        do{
//            System.out.print("Enter UserName : ");
//            userName = Client.sc.nextLine();
//            out.write(userName);
//            out.newLine();
//            out.flush();
//            if(!in.readLine().equals("ok")){
//                System.out.println(" This UserName is Already in Use. Try another.");
//            }
//            else{
//                break;
//            }
//        } while(true);
//        String emailID;
//        do{
//            System.out.print("Enter Email ID : ");
//            emailID = Client.sc.nextLine();
//            out.write(emailID);
//            out.newLine();
//            out.flush();
//            if(!in.readLine().equals("ok")){
//                System.out.println(" This Email Address Already in Use. Try another.");
//            }
//            else{
//                break;
//            }
//        } while(true);
//        String password,confirmPassword;
//        do {
//            System.out.print("Enter Password : ");
//            password = Client.sc.nextLine();
//            System.out.print("Confirm Password : ");
//            confirmPassword = Client.sc.nextLine();
//            if(password.equals(confirmPassword)){
//                break;
//            }
//            else{
//                System.out.println(" Passwords do not match");
//            }
//        } while (true);
//        System.out.println(" Enter Date of Birth (DD-MM-YYYY) : ");
//        String dateOfBirth = Client.sc.nextLine();
//        Account  newAccount = new Account(name,password,userName,dateOfBirth,emailID);
//        out.write(gson.toJson(newAccount));
//        out.newLine();
//        out.flush();
//        userAccount = newAccount;
//        userAccount.tempMsg = new Message();
//        return true;
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


    public boolean isValid(String username) throws IOException{
        out.write("check validity for new direct chat\n");
        out.flush();
        out.write(username);
        String response = in.readLine();

        if(response.equals("true")){
            return true;
        }
        else{
            return false;
        }

    }

//    public static void main(String[] args) throws UnknownHostException, IOException,Exception {
//        Application.launch(ClientApplication.class, args);
//    }

}
