import java.util.ArrayList;

public class Account extends PersonelData {
    String email;
    String password;
    ArrayList<DirectChat>  direct_chats = new ArrayList<>();
    ArrayList<GroupChat> group_chats = new ArrayList<>();
    Message tempMsg;
    public Account(String name, String password,String userName,String dateOfBirth,String email){
        super(name,userName,dateOfBirth);
        this.password=password;
        this.email = email;
    }
    public void createDirectChat(String recieverUserName,Message msg) {
        DirectChat dc = new DirectChat(recieverUserName,msg);
        direct_chats.add(dc);
    }
}
