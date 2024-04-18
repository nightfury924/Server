import java.io.*;
import java.net.*;
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

    private void printMessages(){
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
                Message msgIncoming;
                while(socket.isConnected()){
                    try {
                        msgIncoming = gson.fromJson(in.readLine(), Message.class);
                        System.out.println(msgIncoming.sender + " : "+ msgIncoming.text);
                        addMessage(msgIncoming);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void addMessage(Message incMsg){
        
    }

    private void  sendMessage(){
        try{
            // Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                System.out.print(" Enter Recipient  : ");
                String recipient = Client.sc.nextLine();
                System.out.print("Enter your message: ");
                String text = Client.sc.nextLine();
                userAccount.tempMsg.text = text;
                userAccount.tempMsg.sender = userAccount.getUsername();
                userAccount.tempMsg.receiver = recipient;
                userAccount.tempMsg.chatType = true;
                String outgoing = gson.toJson(userAccount.tempMsg);
                out.write(outgoing);
                out.newLine();
                out.flush();
            }
        }catch(Exception e){
            System.err.println("Error sending message: " + e.getMessage());
        }
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
    }



    static Scanner sc = new Scanner(System.in);
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
        client.sendMessage();
        sc.close();
        
    }
    
}