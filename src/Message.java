import java.sql.Time;

public class Message {
    Time time;
    String text;
    boolean chatType; // true for directChat and false for groupchat
    String sender;
    String receiver;

    public void clear() {
        this.time = null;
        this.text = "";
        this.chatType = false;
        this.sender = "";
        this.receiver = "";
      }
}

