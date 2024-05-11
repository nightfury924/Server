package org.example;

import java.io.*;
import java.net.*;
import java.util.*;


import com.google.gson.Gson;

import static org.example.Server.storeAccount;
import static org.example.Server.storeUserName;
// import com.google.gson.JsonSyntaxException;

public class ClientHandler implements Runnable{
    static private final ArrayList<ClientHandler> allClients = new ArrayList<>();
    static ArrayList<Account> allRegisteredAccounts = new ArrayList<>();
    static ArrayList<GroupChat> allGroupChats = new ArrayList<>();
//    private static ArrayList<DirectChat> allDirectChats = new ArrayList<>();
    private static final Gson gson = new Gson();
    Account userAccount;
    private final Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    int changeCount;

    static {
        Account account = new Account("John Doe", "password123", "johndoe123", "1990-01-01", "johndoe123@example.com");
        Account account2 = new Account("ABC 123", "password123", "abced123", "1990-01-01", "abc123@example.com");
        Account account1 = new Account("poing", "password123", "cpoing123", "12-1-2022", "poingkiport@gmail.com");


        GroupChat gc1 = new GroupChat("ping pong", "123abc45");
        GroupChat gc2 = new GroupChat("ping pong31", "12346abc");
        GroupChat gc3 = new GroupChat("ping123 pong", "123ab4646c");
        GroupChat gc4 = new GroupChat("ping 43pong", "123a45bc");

        gc3.joinGroup("123ab4646c", "cpoing123");
        gc3.joinGroup("123ab4646c", "johndoe123");
        gc3.joinGroup("123ab4646c", "abced123");

        Message gcm1 = new Message("ping123 pong", "cpoing123", false, "hi");
        Message gcm2 = new Message("ping123 pong", "johndoe123", false, "hello");
        Message gcm3 = new Message("ping123 pong", "abced123", false, "hallo");

        gc3.messages.add(gcm1);
        gc3.messages.add(gcm2);
        gc3.messages.add(gcm3);

        allGroupChats.add(gc1);
        allGroupChats.add(gc2);
        allGroupChats.add(gc3);
        allGroupChats.add(gc4);

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
        allRegisteredAccounts.add(account);
        allRegisteredAccounts.add(account1);
        allRegisteredAccounts.add(account2);
//        allDirectChats.add(dc1);
//        allDirectChats.add(dc2);

    }


    public  ClientHandler(Socket socket){
        this.clientSocket = socket;
        this.changeCount = 0;
        try {
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() throws IOException{
        String userName = in.readLine();
        String password = in.readLine();
        System.out.println(" UserName : "+userName+"    Password: " + password );
        if(!isValidUser(userName,password)){
            out.write("false\n");
            out.flush();
            return ;
        }
        out.write("true");
        out.newLine();
        System.out.println(" Sent true ..");
        out.flush();
        System.out.println(".....");
        sendUser();
        checkAndSendGroupChats();
//        checkAndSendDirectChats();
    }

    private void signUp() throws Exception{
        boolean valid;
        do {
            valid = checkUserNameAvailability(in.readLine(),0); // checks if userName is available
            if(valid){
                out.write("ok\n");
                out.flush();
                break;
            }
            else{
                out.write("duplicate\n");
                out.flush();
            }
        } while (true);
        do {
            valid = checkEmail(in.readLine()); // checks if userName is available
            if(valid){
                out.write("ok\n");
                out.flush();
                break;
            }
            else{
                out.write("duplicate\n");
                out.flush();
            }
        } while (true);
        Account account = gson.fromJson(in.readLine(), Account.class);
        allRegisteredAccounts.add(account);
        storeAccount(account);
        storeUserName(account.getUsername());
        this.userAccount = account;
    }

    private boolean checkUserNameAvailability(String name, int flag){
        if(flag == 0){
            for (Account account : allRegisteredAccounts) {
                if(account.getUsername().equals(name)){
                    return false;
                }
            }
            return true;
        }
        else{
            for (Account account : allRegisteredAccounts) {
                if(account.getUsername().equals(name)){
                    return true;
                }
            }
            return true;
        }
    }

    private boolean checkEmail(String mail){
        for (Account account : allRegisteredAccounts) {
            if(account.email.equals(mail)){
                return false;
            }
        }
        return true;
    }

    private boolean isValidUser(String username,String password){
        for (Account account : allRegisteredAccounts) {
            if(account.getUsername().equals(username) && account.password.equals(password)) {
                this.userAccount = account;
                allClients.add(this);
                return true;
            }
        }
        return false;
    }


    private void sendUser() throws IOException{
        String jsonAccount = gson.toJson(userAccount);
        System.out.println(" Sending User To client");
        out.write(jsonAccount);
        out.newLine();
        out.flush();
    }

    private void checkAndSendGroupChats() throws IOException{
        for (GroupChat gp : allGroupChats) {
            if(gp.isMember(this.userAccount.getUsername())){
                out.write(gson.toJson(gp));
                out.newLine();
                out.flush();
            }
        }
        System.out.println("finished sending groupchats");
        out.write("Finished Sending GroupChats");
        out.newLine();
        out.flush();
    }

//    private void checkAndSendDirectChats() throws IOException{
//        for (DirectChat dc : allDirectChats) {
//            if(dc.participants[0].equals(this.userAccount.getUsername()) || dc.participants[1].equals(this.userAccount.getUsername())){
//                out.write(gson.toJson(dc));
//                out.newLine();
//                out.flush();
//            }
//        }
//        System.out.println("finished sending directchats");
//        out.write("Finished Sending directChats");
//        out.newLine();
//        out.flush();
//    }


    @Override
    public void run(){
        try {
            String logOrSign = in.readLine();
            if(logOrSign.equals("login")){
                login();
            }
            else if(logOrSign.equals("signUp")){
                signUp();
            }
            String incomingMessage;
            while (clientSocket.isConnected()){
                incomingMessage = in.readLine();
                this.changeCount++;
                if(incomingMessage.equals("new Group Chat")){
                    addGroupChat();
                }else if(incomingMessage.equals("Adding an admin")){
                    makingAdmin();
                }else if(incomingMessage.equals("leaving Group")){
                    leavingGroup();
                }else if(incomingMessage.equals("group message deleted")){
                    deleteGroupMessage();
                }else if(incomingMessage.equals("direct message deleted")){
                    deleteDirectMessage();
                }else if(incomingMessage.equals("direct message edited")){
                    editDirectMessage();
                }else if(incomingMessage.equals("group message edited")){
                    editGroupMessage();
                }else if(incomingMessage.equals("entering a server")){
                    enteringServer();
                }else if(incomingMessage.equals("start a new direct chat")){
                    startNewPrivateChat();
                }else{
                    Message incMsg = ClientHandler.gson.fromJson(incomingMessage, Message.class);
                    System.out.println("sending Message");
                    sendMessage(incMsg);
                }
                if(changeCount > 5){
                    Server.storeAccount(this.userAccount);
                    changeCount = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                allClients.remove(this);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startNewPrivateChat() throws IOException{
        String recepientName = in.readLine();
        int i = 0;
        for(ClientHandler ct : allClients){
            if(ct.userAccount.getUsername().equals(recepientName)){
                DirectChat dc = new DirectChat(recepientName, this.userAccount.getUsername(), new Message(recepientName,this.userAccount.getUsername(),true,"Start of messages between "+recepientName+" and "+this.userAccount.getUsername()));
                this.userAccount.direct_chats.add(dc);
                ct.userAccount.direct_chats.add(dc);
                ct.out.write("add a new direct chat\n");
                ct.out.flush();
                ct.out.write(gson.toJson(dc));
                ct.out.newLine();
                ct.out.flush();
                this.out.write("add a new direct chat\n");
                this.out.flush();
                this.out.write(gson.toJson(dc));
                this.out.newLine();
                this.out.flush();
                i++;
                break;
            }
        }
        if(i == 0){
            for (Account account : allRegisteredAccounts) {
                if(account.getUsername().equals(recepientName)){
                    DirectChat dc = new DirectChat(recepientName, this.userAccount.getUsername(), new Message(recepientName,this.userAccount.getUsername(),true,"Start of messages between "+recepientName+" and "+this.userAccount.getUsername()));
                    this.userAccount.direct_chats.add(dc);
                    account.direct_chats.add(dc);
                    this.out.write("add a new direct chat\n");
                    this.out.flush();
                    this.out.write(gson.toJson(dc));
                    this.out.newLine();
                    this.out.flush();
                    break;
                }
            }
        }
    }

    private void enteringServer() throws Exception{
        String code = in.readLine();
        int i = 0;
        for (GroupChat gp : allGroupChats) {
            if(gp.joinGroup(code, this.userAccount.getUsername())){
                this.out.write("group joined successfully\n");
                this.out.flush();
                this.out.write(gson.toJson(gp));
                this.out.newLine();
                this.out.flush();
                i++;
                break;
            }
        }
        if(i == 0){
            this.out.write("no matching group\n");
            this.out.flush();
        }
    }

    private void addGroupChat() throws Exception{
        GroupChat tmpGp = gson.fromJson(in.readLine(), GroupChat.class);
        allGroupChats.add(tmpGp);
        Server.storeGroupChatName(tmpGp.groupName);
        System.out.println(" New Group Chat Created.");
    }

    private void makingAdmin() throws Exception{
        String groupChatName = in.readLine();
        String memberName = in.readLine();
        for (GroupChat group : allGroupChats) {
            if(group.groupName.equals(groupChatName)){
                group.admins.add(memberName);
                broadCastUpdatedGroup(group);
            }
        }
    }

    private void leavingGroup()throws Exception{
        String groupChatName = in.readLine();
        String memberName = in.readLine();
        for (GroupChat group : allGroupChats) {
            if(group.groupName.equals(groupChatName)){
                group.members.remove(memberName);
                group.admins.remove(memberName);
                broadCastUpdatedGroup(group);
            }
        }
    }

    private void broadCastUpdatedGroup(GroupChat group) throws Exception{
        for (ClientHandler cl : allClients) {
            for(String member : group.members){
                if(cl.userAccount.getUsername().equals(member) && !(this.userAccount.getUsername().equals(member))){
                    cl.out.write("update Group Chat");
                    cl.out.newLine();
                    cl.out.flush();
                    cl.out.write(gson.toJson(group));
                    cl.out.newLine();
                    cl.out.flush();
                }
            }
        }
    }

    private void deleteGroupMessage() throws Exception{
        String groupChatName = in.readLine();
        Message msgToDelete = gson.fromJson(in.readLine(), Message.class);
        for (GroupChat group : allGroupChats) {
            if(group.groupName.equals(groupChatName)){
                group.messages.remove(msgToDelete);
                broadCastUpdatedGroup(group);
            }
        }
    }

    private void deleteDirectMessage() throws Exception{
        Message msgToDelete = gson.fromJson(in.readLine(), Message.class);
        for (DirectChat dc : this.userAccount.direct_chats) {
            if((dc.participants[0].equals(msgToDelete.receiver) || dc.participants[1].equals(msgToDelete.receiver))){
                dc.messages.remove(msgToDelete);
                break;
            }
        }
        if(msgToDelete.sender.equals(this.userAccount.getUsername())){
            for (ClientHandler cl : allClients){
                if(cl.userAccount.getUsername().equals(msgToDelete.receiver)){
                    for (DirectChat dc : cl.userAccount.direct_chats) {
                        if(dc.participants[0].equals(msgToDelete.sender) || dc.participants[1].equals(msgToDelete.sender)){
                            dc.messages.remove(msgToDelete);
                            cl.out.write("update direct chat");
                            cl.out.newLine();
                            cl.out.flush();
                            cl.out.write(gson.toJson(dc));
                            cl.out.newLine();
                            cl.out.flush();
                            break;
                        }
                    }
                }
            }
        }

    }

    private void editDirectMessage() throws Exception{
        Message msgToEdit = gson.fromJson(in.readLine(), Message.class);
        String newMsg = in.readLine();
        for (DirectChat dc : this.userAccount.direct_chats) {
            if((dc.participants[0].equals(msgToEdit.receiver) || dc.participants[1].equals(msgToEdit.receiver))){
                for (Message msg : dc.messages) {
                    if(msg.equals(msgToEdit)){
                        msg.text = newMsg;
                        break;
                    }
                }
                break;
            }
        }
        for (ClientHandler cl : allClients){
            if(cl.userAccount.getUsername().equals(msgToEdit.receiver)){
                for (DirectChat dc : cl.userAccount.direct_chats) {
                    if((dc.participants[0].equals(msgToEdit.sender) || dc.participants[1].equals(msgToEdit.sender))){
                        for(Message msg : dc.messages){
                            if(msg.equals(msgToEdit)){
                                msg.text = newMsg;
                                break;
                            }
                        }
                        cl.out.write("update direct chat");
                        cl.out.newLine();
                        cl.out.flush();
                        cl.out.write(gson.toJson(dc));
                        cl.out.newLine();
                        cl.out.flush();
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void editGroupMessage() throws Exception{
        String groupName = in.readLine();
        Message msgToEdit = gson.fromJson(in.readLine(), Message.class);
        String newMsg = in.readLine();
        for (GroupChat gp : allGroupChats) {
            if(gp.groupName.equals(groupName)){
                for (Message msg : gp.messages) {
                    if(msg.equals(msgToEdit)) {
                        msg.text = newMsg;
                        broadCastUpdatedGroup(gp);
                        break;
                    }
                }
                break;
            }
        }

    }

    private void sendMessage(Message message) {
        try {
            if(message.chatType) {
                System.out.println("Direct Message");
                addDirectMessageForServer(message, this.userAccount);
                int i = 0;
                for (ClientHandler clientHandler : allClients) {
                    if(clientHandler.userAccount.getUsername().equals(message.receiver)){
                        addDirectMessageForServer(message, clientHandler.userAccount);
                        System.out.println("sent message to "+clientHandler.userAccount.getUsername());
                        clientHandler.out.write(gson.toJson(message));
                        clientHandler.out.newLine();
                        clientHandler.out.flush();
                        i++;
                    }
                }
                if(i==0){
                    for (Account account : allRegisteredAccounts) {
                        if(account.getUsername().equals(message.receiver)){
                            addDirectMessageForServer(message, account);
                        }
                    }
                }
            }
            else{
                for (GroupChat gp : allGroupChats) {
                    if(gp.groupName.equals(message.receiver)){
                        for(ClientHandler cl : allClients){
                            for(String user : gp.members){
                                if(user.equals(cl.userAccount.getUsername())){
                                    out.write(gson.toJson(message));
                                    out.newLine();
                                    out.flush();
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addDirectMessageForServer(Message message, Account accountToAddMessage){
        int i = 0;
        for (DirectChat dc : accountToAddMessage.direct_chats) {
            if( (dc.participants[0].equals(message.receiver) && dc.participants[1].equals(message.sender) || (dc.participants[1].equals(message.receiver) && dc.participants[0].equals(message.sender) ))){
                i++;
                dc.messages.add(message);
                break;
            }
        }
//        if(i == 0){
//            accountToAddMessage.createDirectChat(message.receiver, message.sender, message);
//        }
    }


}