import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;

class ClientHandler implements Runnable{
    private static ArrayList<ClientHandler> allClients = new ArrayList<>();
    private static ArrayList<Account> allRegisteredAccounts = new ArrayList<>();
    private static Gson gson = new Gson();
    static {
        Account account = new Account("John Doe", "password123", "johndoe", "1990-01-01", "johndoe@example.com");
        Account account1 = new Account("poing", "password123", "cpoing", "12-1-2022", "poingkiport");
        allRegisteredAccounts.add(account);
        allRegisteredAccounts.add(account1);
    }
    Account userAccount;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
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
    @Override
    public void run(){
        try {
            login();
            String gsonMessage;
            while (clientSocket.isConnected()){
                gsonMessage = in.readLine();
                sendMessage(gson.fromJson(gsonMessage, Message.class));
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
    private void sendMessage(Message message) {
        try {
            for (ClientHandler clientHandler : allClients) {
                if(clientHandler.userAccount.getUsername().equals(message.receiver)){
                    addMessageforSenderServerSide(message);
                    addMessageforRecieverServerSide(message, clientHandler);
                    clientHandler.out.write(gson.toJson(message));
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    private void addMessageforSenderServerSide(Message message){
        if(message.chatType){
            int i = 0;
            for (DirectChat dc : userAccount.direct_chats) {
                if(dc.username.equals(message.receiver)){
                    i++;
                    dc.messages.add(message);
                }
            }
            if(i == 0){
                userAccount.createDirectChat(message.receiver, message);
            }
        }
    }
    private void addMessageforRecieverServerSide(Message message, ClientHandler clientHandler){
        
    }
}