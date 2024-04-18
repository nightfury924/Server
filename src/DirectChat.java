import java.util.ArrayList;

public class DirectChat {
    String username;
    ArrayList<Message> messages = new ArrayList<>();
    public DirectChat(String username, Message initialMessage){
        this.username = username;
        messages.add(initialMessage);
    }
}
