package org.example;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import com.google.gson.Gson;

public class FileStorage{
    static Account davidAccount;
    static Account aliceAccount;
    static Account johnAccount;
    static ArrayList<Account> accounts = new ArrayList<>();
    static Gson gson = new Gson();


//    public static void startUp()
    static {
        PersonelData johnData = new PersonelData("John Doe", "john.doe", "1990-01-01");
        PersonelData janeData = new PersonelData("Jane Smith", "jane.smith", "1995-12-31");


        Message message1 = new Message("john.doe", "jane.smith", true, "Hello there!"); // Direct chat message from John to Jane
   
        Message message2 = new Message("jane.smith", "john.doe", true, "Hi John!"); // Direct chat reply from Jane to John
    
        Message message3 = new Message("John Doe", "Programmers Paradise", false, "This is a message to the group chat!"); // Group chat message from John

        GroupChat group1 = new GroupChat("Programmers Paradise", "secret123");
        group1.admins.add("john.doe");  // Assuming John Doe is an admin
        group1.members.add("jane.smith");
        group1.messages.add(message3);

        DirectChat chat1 = new DirectChat("john.doe", "jane.smith", message1);

        johnAccount = new Account(johnData.getName(), "password123", johnData.getUsername(), johnData.getDateOfBirth(), "john.doe@email.com");
        johnAccount.createDirectChat(johnData.getUsername(), janeData.getUsername(), message2);  // John replies in the direct chat
        johnAccount.addGroupChat(group1);
PersonelData aliceData = new PersonelData("Alice Jones", "alice.jones", "2000-05-15");
Message message4 = new Message("alice.jones", "john.doe", true, "Hey John, how are you doing?");
message4.time = new Time(System.currentTimeMillis()); // Set timestamp dynamically

Message message5 = new Message("John Doe", "alice.jones", true, "Hi Alice, I'm doing well! Thanks for asking.");
message5.time = new Time(System.currentTimeMillis()); // Set timestamp dynamically

Message message6 = new Message("alice.jones", "Programmers Paradise", false, "This is a message from Alice to the group!");
message6.time = new Time(System.currentTimeMillis()); // Set timestamp dynamically


DirectChat chat2 = new DirectChat("alice.jones", "john.doe", message4);

// Alice's Group Chat (assuming she joins the same group)
aliceAccount = new Account(aliceData.getName(), "password456", aliceData.getUsername(), aliceData.getDateOfBirth(), "alice.jones@email.com");
aliceAccount.createDirectChat(aliceData.getUsername(), johnData.getUsername(), message5); // Reply to John's message
aliceAccount.addGroupChat(group1); // Join the same group as John

group1.messages.add(message6);

PersonelData davidData = new PersonelData("David Lee", "david.lee", "1985-08-21");

Message message7 = new Message("david.lee", "jane.smith", true, "Hi Jane, nice to meet you!");
message7.time = new Time(System.currentTimeMillis()); // Set timestamp dynamically

Message message8 = new Message("jane.smith", "david.lee", true, "Hi David! Welcome!");
message8.time = new Time(System.currentTimeMillis()); // Set timestamp dynamically

DirectChat chat3 = new DirectChat("david.lee", "jane.smith", message7);

GroupChat group2 = new GroupChat("Coffee Lovers", "coffeelover123");
group2.admins.add("david.lee");  // Assuming David Lee is an admin of his group

// David's Account
davidAccount = new Account(davidData.getName(), "password789", davidData.getUsername(), davidData.getDateOfBirth(), "david.lee@email.com");
davidAccount.createDirectChat(davidData.getUsername(), janeData.getUsername(), message8); // Reply to Jane's message
davidAccount.addGroupChat(group2); // Create a new group

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        startUp();
        accounts.add(davidAccount);
        accounts.add(aliceAccount);
        accounts.add(johnAccount);
//        storeUserAccount(accounts);
//        readUserAccount();
    }



//    static private void storeUserAccount(ArrayList<Account> accounts) throws IOException{
//        BufferedWriter userNameWriter = new BufferedWriter(new FileWriter("userNames.txt",false));
//        for (Account account : accounts) {
//            userNameWriter.write(account.getUsername());
//            userNameWriter.newLine();
//            userNameWriter.flush();
//            ObjectOutputStream objectWriter = new ObjectOutputStream(new FileOutputStream(account.getUsername()+".bin"));
//            objectWriter.writeObject(account);
//            objectWriter.flush();
//            objectWriter.close();
//        }
//        userNameWriter.close();
//
//    }
//    static private void readUserAccount() throws IOException, ClassNotFoundException {
//        ArrayList<Account> userAccounts = new ArrayList<>();
//        BufferedReader userNameReader = new BufferedReader(new FileReader("userNames.txt"));
//        ArrayList<String> userNames = new ArrayList<>();
//        String temp;
//        while((temp = userNameReader.readLine()) != null){
//            userNames.add(temp);
//        }
//        for (String userName : userNames){
//            ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(userName+".bin"));
//            Account tempAccount = (Account) (objectReader.readObject());
//            userAccounts.add(tempAccount);
//        }
//    }


    

}