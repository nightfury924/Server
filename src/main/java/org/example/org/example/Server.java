package org.example;

import java.io.*;
import java.lang.ref.Cleaner;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static final int port = 27508;
    private ServerSocket serverSocket;
    private static ObjectInputStream objectReader;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        new Thread(() -> {
            while (!serverSocket.isClosed()){
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(" Client Connected");
                    ClientHandler clientHandler = new ClientHandler(socket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        serverStartUp();
        ServerSocket serverSocket = new ServerSocket(port);
        Server  server = new Server(serverSocket);
        server.startServer();
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print(" Admin Command: ");
            String command = scanner.nextLine();
            if(command.equals("Server.execute.shutDown")){
                serverShutdown();
            }
        }

    }
    private static void serverStartUp() throws IOException, ClassNotFoundException {
        ClientHandler.allRegisteredAccounts = loadUserAccounts();
        ClientHandler.allGroupChats = loadGroupChats();
    }
    private static void serverShutdown() throws IOException {
        for (Account account : ClientHandler.allRegisteredAccounts) {
            storeAccount(account);
        }
        for(GroupChat gc : ClientHandler.allGroupChats){
            storeGroupChat(gc);
        }
        System.exit(0);
    }

    static public void storeAccount(Account account) throws IOException{
        ObjectOutputStream objectWriter = new ObjectOutputStream(new FileOutputStream("Accounts/"+account.getUsername()+".bin"));
        objectWriter.writeObject(account);
        objectWriter.flush();
        objectWriter.close();
    }

    static public void storeUserName(String userName) throws IOException {
        BufferedWriter userNameWriter = new BufferedWriter(new FileWriter("Names/userNames.txt",true));
        userNameWriter.write(userName);
        userNameWriter.newLine();
        userNameWriter.flush();
        userNameWriter.close();
    }
    static public void storeGroupChatName(String groupChatName) throws IOException {
        BufferedWriter groupNameWriter = new BufferedWriter(new FileWriter("Names/groupChatNames.txt",true));
        groupNameWriter.write(groupChatName);
        groupNameWriter.newLine();
        groupNameWriter.flush();
        groupNameWriter.close();
    }
    static private ArrayList<GroupChat> loadGroupChats() throws IOException, ClassNotFoundException {
        ArrayList<GroupChat> groupChats = new ArrayList<>();
        BufferedReader groupNameReader = new BufferedReader(new FileReader("Names/groupChatNames.txt"));
        String groupChatName;
        while(( groupChatName = groupNameReader.readLine()) != null){
            objectReader = new ObjectInputStream(new FileInputStream("GroupChats/"+groupChatName+".bin"));
            groupChats.add( (GroupChat) (objectReader.readObject()) );
            objectReader.close();
        }
        groupNameReader.close();
        return groupChats;
    }

    static private void storeGroupChat(GroupChat groupChat) throws IOException {
        ObjectOutputStream objectWriter = new ObjectOutputStream(new FileOutputStream("GroupChats/"+groupChat.groupName+".bin"));
        objectWriter.writeObject(groupChat);
        objectWriter.flush();
        objectWriter.close();
    }

    static private ArrayList<Account> loadUserAccounts() throws IOException, ClassNotFoundException {
        ArrayList<Account> userAccounts = new ArrayList<>();
        BufferedReader userNameReader = new BufferedReader(new FileReader("Names/userNames.txt"));
        String userName;
        while(( userName = userNameReader.readLine()) != null){
            objectReader = new ObjectInputStream(new FileInputStream("Accounts/"+userName+".bin"));
            userAccounts.add((Account) (objectReader.readObject()));
            objectReader.close();
        }
        userNameReader.close();
        return userAccounts;
    }
}

