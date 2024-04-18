import java.sql.Time;

public class Message {
    Time time;
    String text;
    boolean chatType; // true for directChat and false for groupchat
    String sender;
    String receiver;
}

