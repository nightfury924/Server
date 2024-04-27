import java.io.*;
import java.net.*;
// import java.util.ArrayList;
import java.util.Scanner;

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
            System.out.println("-----------------------------------------------------------");
            System.out.println("Participent 1: " + dc.participants[0] + "\tParticipent 2: " + dc.participants[1]);
            System.out.println("-----------------------------------------------------------");
            dc.displayChat();
            System.out.println("-----------------------------------------------------------");
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
                        }else if(input.equals("update direct chat")){
                            updateDirectChat(in.readLine());
                            input = "";
                        }else if(input.equals("add a new direct chat")){
                            DirectChat dc = gson.fromJson(in.readLine(), DirectChat.class);
                            userAccount.direct_chats.add(dc);
                            input = "";
                        }else{
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
                updateDc.displayChat();
                dc = updateDc;
                dc.displayChat();
                break;
            }
        }
    }

    

    private void interact(){
        // Scanner scanner = new Scanner(System.in);
        while(socket.isConnected()){
            try {
                int choice;
                System.out.println(" 1.Direct Chat");
                System.out.println(" 2.Group Chat");
                System.out.println(" 3.Manage group");
                System.out.println(" 4.Create Group");
                System.out.println(" 5.Disply DIrect Chat");
                System.out.print(" Choice: ");
                choice = Client.sc.nextInt();
                Client.sc.nextLine();
                if(choice == 1){
                    System.out.println(" 1.Strat a New Direct Chat");
                    System.out.println(" 2.Send message");
                    System.out.println(" 3.Delete Message");
                    System.out.println(" 4.Edit Message");
                    System.out.print(" Choice : ");
                    choice = Client.sc.nextInt();
                    Client.sc.nextLine();
                    if(choice == 1){
                        startNewDirectChat();
                    }else if(choice ==2){
                        sendDirectMessage();
                    }
                    else if(choice==3){
                        deleteDirectMessage();
                    }
                    else if(choice == 4){
                        editMessage();
                    }
                }
                else if(choice == 2){
                    System.out.println(" 1.Send message");
                    System.out.println(" 2.Join Group");
                    System.out.print(" Choice : ");
                    choice = Client.sc.nextInt();
                    Client.sc.nextLine();
                    if(choice ==1){
                        sendGroupMessage();
                    }
                    else if(choice==2){
                        joinGroup();
                    }
                    
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
                                System.out.println(" 5. Edit Message");
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
                                    case 5:
                                        editGroupMessage(gp);
                                        break;
                                }
                            }
                            else{
                                System.out.println(" 1. Leave Group"); // member options
                                System.out.println(" 2. Delete Message"); // can only delete own message
                                System.out.println(" 3. Edit Message");
                                choice = Client.sc.nextInt();
                                Client.sc.nextLine();
                                switch(choice){
                                    case 1: 
                                        leaveGroup(gp);
                                        break;
                                    case 2: 
                                        deleteGroupMessage(gp);
                                        break;
                                    case 5:
                                        editGroupMessage(gp);
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
                else if(choice == 5){
                    printMessages();
                }
                else{
                    System.out.println("Invalid Choice!");
                }
            } catch (Exception e) {
                System.err.println("Error sending message: " + e.getMessage());
                Client.sc.nextLine();
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
    
    public void editGroupMessage(GroupChat gp) throws Exception{
        gp.displayChat();
        System.out.print("\n Select Message to Edit: ");
        int index = Client.sc.nextInt() - 1;
        Client.sc.nextLine();
        Message msg = gp.messages.get(index);
        if(msg.sender.equals(userAccount.getUsername())){
            System.out.print(" Enter New Message : ");
            String newMsg = Client.sc.nextLine();      
            out.write("group message edited");
            out.newLine();
            out.flush();
            out.write(gp.groupName);
            out.newLine();
            out.flush();
            out.write(gson.toJson(msg));
            out.newLine();
            out.flush();
            out.write(newMsg);
            out.newLine();
            out.flush();
            gp.messages.get(index).text = newMsg;
        }
        else{
            System.out.println(" Can only edit own messages.");
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
                
                if(msg.sender.equals(this.userAccount.getUsername())){    // edit from both users chat list
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

    private void startNewDirectChat() throws IOException{
        System.out.print("Enter Username of person to send a direct message to : ");
        String username = Client.sc.nextLine();
        out.write("start a new direct chat\n");
        out.flush();
        out.write(username);
        out.newLine();
        out.flush();
    }

    private void sendDirectMessage() throws IOException, InterruptedException{
        System.out.print(" Enter Recipient  : ");
        String recipient = Client.sc.nextLine();
        System.out.print("Enter your message: ");
        String text = Client.sc.nextLine();
        userAccount.tempMsg.text = text;
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

    private boolean addMessage(Message incMsg){
        if(incMsg.chatType){
            for (DirectChat dc : userAccount.direct_chats) {
                if(dc.participants[0].equals(incMsg.receiver) || dc.participants[1].equals(incMsg.receiver)){
                    dc.messages.add(incMsg);
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

    private boolean validateGroupName(String groupName){
        for (GroupChat gp : userAccount.group_chats) {
            if (gp.groupName.equals(groupName)){
                return true;
            }
        }
        return false;
    }


    //authorization has problems if rejected program just hangs
    private boolean login() throws IOException{
        out.write("login\n");
        out.flush();
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

    private boolean signUp() throws Exception{
        out.write("signUp\n");
        out.flush();
        System.out.print("Enter Name : ");
        String name = Client.sc.nextLine();
        String userName;
        do{
            System.out.print("Enter UserName : ");
            userName = Client.sc.nextLine();
            out.write(userName);
            out.newLine();
            out.flush();
            if(!in.readLine().equals("ok")){
                System.out.println(" This UserName is Already in Use. Try another.");
            }
            else{
                break;
            }
        } while(true);
        String emailID;
        do{
            System.out.print("Enter Email ID : ");
            emailID = Client.sc.nextLine();
            out.write(emailID);
            out.newLine();
            out.flush();
            if(!in.readLine().equals("ok")){
                System.out.println(" This Email Address Already in Use. Try another.");
            }
            else{
                break;
            }
        } while(true);
        String password,confirmPassword;
        do {   
            System.out.print("Enter Password : ");
            password = Client.sc.nextLine();
            System.out.print("Confirm Password : ");
            confirmPassword = Client.sc.nextLine();
            if(password.equals(confirmPassword)){
                break;
            }
            else{
                System.out.println(" Passwords do not match");
            }
        } while (true);
        System.out.println(" Enter Date of Birth (DD-MM-YYYY) : ");
        String dateOfBirth = Client.sc.nextLine();
        Account  newAccount = new Account(name,password,userName,dateOfBirth,emailID);
        out.write(gson.toJson(newAccount));
        out.newLine();
        out.flush();
        userAccount = newAccount;
        userAccount.tempMsg = new Message();
        return true;
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

    public static void main(String[] args) throws UnknownHostException, IOException,Exception {
        // String username = sc.nextLine();
        Socket socKet=new Socket(SERVER_IP, SERVER_PORT);;
        Client client = new Client(socKet);
        System.out.println(" 1.Login");
        System.out.println(" 2.SignUP");
        System.out.print(" Choose : ");
        int ch = sc.nextInt();
        sc.nextLine();
        boolean authenticated;
        if(ch==1){
            authenticated = client.login();
        }
        else if(ch == 2){
            authenticated = client.signUp();
        }
        else{
            authenticated = false;
        }
        
        if(!authenticated){
            System.exit(SERVER_PORT);
        }
        // client.printMessages();
        client.startListening();
        client.interact();
        sc.close();
        
    }
    
}