import java.sql.Time;

public class Message {
    Time time;
    String text;
    boolean chatType; // true for directChat and false for groupchat
    String sender;
    String receiver;

    // public Message(Message msg){
    //   this.time = msg.time;
    //   this.chatType = msg.chatType;
    //   this.text = msg.text;
    //   this.sender = msg.sender;
    //   this.receiver = msg.receiver;
    // }
    public Message(String receiver, String sender, boolean chatType, String text) {
      this.receiver = receiver;
      this.sender = sender;
      this.text = text;
      this.chatType = chatType;
      this.time = new Time(System.currentTimeMillis());

    }
    public Message(){
      time = null;
      text = "";
      chatType = false;
      sender = "";
      receiver = "";
    }
    public void clear() {
      this.time = null;
      this.text = "";
      this.chatType = false;
      this.sender = "";
      this.receiver = "";
    }

}

