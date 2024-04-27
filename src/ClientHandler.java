import java.io.*;
import java.net.*;
import java.util.*;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
            out.write("false\n");
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
                if(incomingMessage.equals("new Group Chat")){
                    addGroupChat();
                    continue;
                }else if(incomingMessage.equals("Adding an admin")){
                    makingAdmin();
                    continue;
                }else if(incomingMessage.equals("leaving Group")){
                    leavingGroup();
                    continue;
                }else if(incomingMessage.equals("group message deleted")){
                    deleteGroupMessage();
                    continue;
                }else if(incomingMessage.equals("direct message deleted")){
                    deleteDirectMessage();
                    continue;
                }else if(incomingMessage.equals("direct message edited")){
                    editDirectMessage();
                    continue;
                }else if(incomingMessage.equals("group message edited")){
                    editGroupMessage();
                    continue;
                }else if(incomingMessage.equals("entering a server")){
                    enteringServer();
                    continue;
                }else if(incomingMessage.equals("start a new direct chat")){
                    startNewPrivateChat();
                    continue;
                }else{
                    Message incMsg = ClientHandler.gson.fromJson(incomingMessage, Message.class);
                    System.out.println("sending Message");
                    sendMessage(incMsg);
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
            }
        }
        if(i == 0){
            accountToAddMessage.createDirectChat(message.receiver, message.sender, message);
        }
    }


}