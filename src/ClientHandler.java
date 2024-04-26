import java.io.*;
import java.net.*;
import java.util.*;


import com.google.gson.Gson;

class ClientHandler implements Runnable{
    private static ArrayList<ClientHandler> allClients = new ArrayList<>();
    private static ArrayList<Account> allRegisteredAccounts = new ArrayList<>();
    private static ArrayList<GroupChat> allGroupChats = new ArrayList<>();
    private static Gson gson = new Gson();
    Account userAccount;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    
    static {
        Account account = new Account("John Doe", "password123", "johndoe", "1990-01-01", "johndoe@example.com");
        Account account1 = new Account("poing", "password123", "cpoing", "12-1-2022", "poingkiport");
        allRegisteredAccounts.add(account);
        allRegisteredAccounts.add(account1);
    }

    
    public  ClientHandler(Socket socket){
        this.clientSocket = socket;
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
        while( !isValidUser(userName,password)){
            out.write("false");
            out.flush();
            userName = in.readLine();
            password = in.readLine();
            System.out.println(" UserName : "+userName+"    Password: " + password );
        }
        out.write("true");
        out.newLine();
        System.out.println(" Sent true ..");
        out.flush();
        System.out.println(".....");
        sendUser();
        checkAndSendGroupChats();
    }


    private boolean isValidUser(String username,String password){
        for (Account account : allRegisteredAccounts) {
            if(account.getUsername().equals(username) && account.password.equals(password)) {
                allClients.add(this);
                userAccount = account;
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


    @Override
    public void run(){
        try {
            login();
            String gsonMessage;
            while (clientSocket.isConnected()){
                gsonMessage = in.readLine();
                if(gsonMessage.equals("new Group Chat")){
                    addGroupChat();
                    continue;
                }else if(gsonMessage.equals("Adding an admin")){
                    makingAdmin();
                    continue;
                }else if(gsonMessage.equals("leaving Group")){
                    leavingGroup();
                    continue;
                }else if(gsonMessage.equals("group message deleted")){
                    deleteGroupMessage();
                    continue;
                }else if(gsonMessage.equals("direct message deleted")){
                    deleteDirectMessage();
                    continue;
                }else if(gsonMessage.equals("direct message edited")){
                    editDirectMessage();
                    continue;
                }else{
                    System.out.println("sending Message");
                    sendMessage(gson.fromJson(gsonMessage, Message.class));
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

    private void addGroupChat() throws Exception{
        GroupChat tmpGp = gson.fromJson(in.readLine(), GroupChat.class);
        allGroupChats.add(tmpGp);
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

    private void editDirectMessage() throws Exception{
        Message msgToEdit = gson.fromJson(in.readLine(), Message.class);
        String newMsg = in.readLine();
        for (DirectChat dc : this.userAccount.direct_chats) {
            if((dc.participants[0].equals(msgToEdit.receiver) || dc.participants[1].equals(msgToEdit.receiver))){
                for (Message msg : dc.messages) {
                    if(msg.equals(msgToEdit)){
                        msg = msgToEdit;
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
                }
            }
            break;
        }
    }
    
    private void sendMessage(Message message) {
        try {
            if(message.chatType) {
                System.out.println("Direct Message");
                addDirectMessageForServer(message, this.userAccount);
                for (ClientHandler clientHandler : allClients) {
                    if(clientHandler.userAccount.getUsername().equals(message.receiver)){
                        addDirectMessageForServer(message, clientHandler.userAccount);
                        System.out.println("sent message to "+clientHandler.userAccount.getUsername());
                        clientHandler.out.write(gson.toJson(message));
                        clientHandler.out.newLine();
                        clientHandler.out.flush();
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
            }
        }
        if(i == 0){
            accountToAddMessage.createDirectChat(message.receiver, message.sender, message);
        }
    }
}