import java.io.*;
import java.net.*;
// import java.util.ArrayList;
import java.util.Scanner;

// import javax.swing.GrayFilter;
// import javax.swing.GroupLayout.Group;

// import org.w3c.dom.UserDataHandler;

import com.google.gson.Gson;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Account userAccount;
    private static Gson  gson = new Gson();
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 27508;
    static Scanner sc = new Scanner(System.in);

    public Client(Socket socket) {
        try {
            this.socket = socket;
            // this.username = username;
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMessages(){        // do this
        for (DirectChat dc : userAccount.direct_chats) {
            for (Message text : dc.messages) {
                System.out.println(text.text);
            }
        }
        
    }

    private void startListening(){
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
                        }
                        else if(input.equals("update direct chat")){
                            updateDirectChat(in.readLine());
                            input = "";
                        }
                        else{
                            msgIncoming = gson.fromJson(input, Message.class);
                            System.out.println(msgIncoming.sender + " : "+ msgIncoming.text);
                            addMessage(msgIncoming);
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void updateGroupChat(String input){
        GroupChat  updateGc = gson.fromJson(input,GroupChat.class);
        for (GroupChat group : userAccount.group_chats) {
            if(updateGc.groupName.equals(group.groupName)){
                group = updateGc;
                break;
            }
        }
    }

    private void updateDirectChat(String input){
        DirectChat updateDc = gson.fromJson(input, DirectChat.class);
        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(updateDc.participants[0]) && dc.participants[1].equals(updateDc.participants[1])){
                dc = updateDc;
            }
        }
    }

    private void interact(){
        try{
            // Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                int choice;
                System.out.println(" 1.Direct Chat");
                System.out.println(" 2.Group Chat");
                System.out.println(" 3.Manage group");
                System.out.println(" 4.Create Group");
                System.out.print(" Choice: ");
                choice = Client.sc.nextInt();
                Client.sc.nextLine();
                if(choice == 1){
                    System.out.println(" 1.Send message");
                    System.out.println(" 2.Delete Message");
                    System.out.println(" 3.Edit Message");
                    System.out.print(" Choice : ");
                    choice = Client.sc.nextInt();
                    Client.sc.nextLine();
                    if(choice ==1){
                        sendDirectMessage();
                    }
                    else if(choice==2){
                        deleteDirectMessage();
                    }
                    else if(choice == 3){
                        editMessage();
                    }
                }
                else if(choice == 2){
                    sendGroupMessage();
                }
                else if(choice == 3){
                    System.out.println(" GroupName: ");
                    String groupName = Client.sc.nextLine();
                    for (GroupChat gp : userAccount.group_chats) {
                        if (gp.groupName.equals(groupName)){
                            if (gp.isAdmin(userAccount.getUsername())){  // admin options
                                System.out.println(" 1. Make an Admin");
                                System.out.println(" 2. Leave Group");
                                System.out.println(" 3. Kick Member");
                                System.out.println(" 4. Delete Message"); // can delete any message
                                choice = Client.sc.nextInt();
                                Client.sc.nextLine(); 
                                switch(choice){
                                    case 1: 
                                        makeAdmin(gp);
                                        break;
                                    case 2: 
                                        if(!(gp.noOfAdmins()>1)){
                                            System.out.println(" You are the only Admin Make Another before Leaving");
                                            makeAdmin(gp);
                                        }
                                        leaveGroup(gp);
                                        break;
                                    case 3: 
                                        kickMember(gp);
                                        break;
                                    case 4: 
                                        deleteGroupMessage(gp);
                                        break;
                                }
                            }
                            else{
                                System.out.println(" 1. Leave Group"); // member options
                                System.out.println(" 2. Delete Message"); // can only delete own message
                                choice = Client.sc.nextInt();
                                Client.sc.nextLine();
                                switch(choice){
                                    case 1: 
                                        leaveGroup(gp);
                                        break;
                                    case 2: 
                                        deleteGroupMessage(gp);
                                        break;
                                }
                            }
                        
                        }
                        System.out.println(" 2.Group Chat");
                        System.out.println(" 3.Manage group");
                    }
                
                }else if(choice == 4){
                    System.out.println(" Enter Group Name: ");
                    String groupName = Client.sc.nextLine();
                    System.out.println(" Create Group Entrance Code: ");
                    String code = Client.sc.nextLine();
                    GroupChat gp = new GroupChat(groupName, code);
                    userAccount.group_chats.add(gp);
                    out.write("new Group Chat\n");
                    out.flush();
                    out.write(gson.toJson(gp));
                    out.newLine();
                    out.flush();
                }
            }
        }catch(Exception e){
            System.err.println("Error sending message: " + e.getMessage());
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
                if(msg.sender == this.userAccount.getUsername()){    // delete from both users chat list
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
                }
            }
            break;
        }
            
    }
    
    public void editMessage() throws Exception{
        System.out.println(" Select the conversation from which you want to delete:");
        String reciever = Client.sc.nextLine();
        for (DirectChat dc : userAccount.direct_chats) {
            if(dc.participants[0].equals(reciever) || dc.participants[1].equals(reciever)){
                dc.displayChat();
                System.out.print("Choose Message to Edit:");
                int index = Client.sc.nextInt();
                Client.sc.nextLine();
                Message msg= dc.messages.get(index-1);
                
                if(msg.sender == this.userAccount.getUsername()){    // edit from both users chat list
                    System.out.print(" Enter New Message : ");
                    String newMsg = Client.sc.nextLine();
                    
                    out.write("direct message edited");
                    out.newLine();
                    out.flush();
                    out.write(gson.toJson(msg));
                    out.newLine();
                    out.flush();
                    out.write(newMsg);
                    out.newLine();
                    out.flush();
                    dc.messages.get(index-1).text = newMsg;
                }
                else{                                              
                    System.out.println(" You can only edit your own messages.");
                }
            }
            break;
        }
    }

    private void sendDirectMessage() throws IOException{
        System.out.print(" Enter Recipient  : ");
        String recipient = Client.sc.nextLine();
        System.out.print("Enter your message: ");
        String text = Client.sc.nextLine();
        userAccount.tempMsg.text = text;
        userAccount.tempMsg.sender = userAccount.getUsername();
        userAccount.tempMsg.receiver = recipient;
        userAccount.tempMsg.chatType = true;
        addMessage(userAccount.tempMsg);
        String outgoing = gson.toJson(userAccount.tempMsg);
        out.write(outgoing);
        out.newLine();
        out.flush();
        // userAccount.tempMsg.clear();
    }

    private void sendGroupMessage() throws IOException{
        System.out.print(" Enter Group Name : ");
        String recipient = Client.sc.nextLine();
        if(!validateGroupName(recipient)){
            System.out.println(" Invaid Group Name.");
            System.out.print(" Enter Group Name : ");
            recipient = Client.sc.nextLine();
        }
        System.out.print("Enter your message: ");
        String text = Client.sc.nextLine();
        userAccount.tempMsg.text = text;
        userAccount.tempMsg.sender = userAccount.getUsername();
        userAccount.tempMsg.receiver = recipient;
        userAccount.tempMsg.chatType = false;
        addMessage(userAccount.tempMsg);
        String outgoing = gson.toJson(userAccount.tempMsg);
        out.write(outgoing);
        out.newLine();
        out.flush();
        userAccount.tempMsg.clear();
    }

    private void addMessage(Message incMsg){
        if(incMsg.chatType){
            int i = 0;
            for (DirectChat dc : userAccount.direct_chats) {
                if(dc.participants[0].equals(incMsg.receiver) || dc.participants[1].equals(incMsg.receiver)){
                    i++;
                    dc.messages.add(incMsg);
                }
            }
            if(i == 0){
                userAccount.createDirectChat(incMsg.receiver, incMsg.sender, incMsg);
            }
        }
        else{
            for (GroupChat gc : userAccount.group_chats){
                if(gc.groupName.equals(incMsg.receiver)){
                    gc.messages.add(incMsg);
                }
            }
        }
    }

    private boolean validateGroupName(String groupName){
        for (GroupChat gp : userAccount.group_chats) {
            if (gp.groupName.equals(groupName)){
                return true;
            }
        }
        return false;
    }


    //authorization has problems if rejected program just hangs
    private boolean authorization() throws IOException{
        for (int i = 1; i <= 3; i++) {
            System.out.println(" Enter UserName: ");
            String temp_userName = Client.sc.nextLine();
            System.out.println(" Password   : ");
            String temp_password = Client.sc.nextLine();
            out.write(temp_userName);
            out.newLine();
            out.flush();
            out.write(temp_password);
            out.newLine();
            out.flush();
            String response = in.readLine();
            if(response.equals("true")){
                receiveAccount();
                return true;
            } else{
                System.out.println(" Authentication Failed Try AGain. Attempts Remaining "+ (3-i));
            }
        }
        socket.close();
        return false;
    }



    private void receiveAccount( )throws IOException{
        String jsonAccount = in.readLine();
        userAccount = gson.fromJson(jsonAccount, Account.class);
        userAccount.tempMsg = new Message();
        System.out.println("User Name :"+userAccount.getUsername());
        receiveGroupChats();
    }

    private void receiveGroupChats() throws IOException{
        String incoming = in.readLine();
        while (!incoming.equals("Finished Sending GroupChats")) {
            GroupChat tmpGroupChat = gson.fromJson(incoming, GroupChat.class);
            userAccount.addGroupChat(tmpGroupChat);
            incoming = in.readLine();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        // String username = sc.nextLine();
        Socket socKet=new Socket(SERVER_IP, SERVER_PORT);;
        Client client = new Client(socKet);
        boolean Authenticated = client.authorization();
        
        if(!Authenticated){
            System.exit(SERVER_PORT);
        }
        client.printMessages();
        client.startListening();
        client.interact();
        sc.close();
        
    }
    
}