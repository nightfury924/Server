import java.util.ArrayList;

public class GroupChat {
    String groupName;
    private final String entranceCode;
    ArrayList<String> admins;
    ArrayList<String> members;
    ArrayList<Message> messages;

    public GroupChat(String name, String code) {
        groupName = name;
        entranceCode = code;
        admins = new ArrayList<String>();
        members = new ArrayList<String>();
        messages = new ArrayList<Message>();
    }

    public boolean isMember(String name){
        return members.contains(name);
    }

    public boolean isAdmin(String name){
        return admins.contains(name);
    }

    public int noOfAdmins(){
        return  admins.size();
    }

    public void removeUser(String name){
        admins.remove(name);
        members.remove(name);
    }

    public void displayChat(){
        int i = 1;
        for (Message message : messages) {
            System.out.println(" "+i+":"+message.sender+" : "+message.text);
            i++;
        }
    }


    public void displayMembers(){
        int i = 1;
        for (String name : members) {
            System.out.println(" "+i+" :"+name);
            i++;
        }
    }

    public boolean joinGroup(String code,String username){
        if(code.equals(entranceCode)){
            members.add(username);
            return true;
        }
        else{
            System.out.println( "Entrance code didn't match");
            return false;
        }
    }

}
