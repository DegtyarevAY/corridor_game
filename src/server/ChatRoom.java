package server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatRoom {

    final String name;
    HashMap<String, User> users = new HashMap<String, User>();
    ArrayList<Message> messages = new ArrayList<Message>();
    Date expiry;

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addMessage(User user, Message message) {
        messages.add(message);
        for (String s : users.keySet()) {
            users.get(s).addMessage(message.toString());
        }
    }

    public ArrayList<Message> getMessages() {
        ArrayList<Message> copy = new ArrayList<Message>(messages);
        return copy;
    }

    public void addUser(User user) {
        
        users.put(user.getToken(), user);
        user.setChatRoom(this.name);
        for (Message message : messages) {
            user.addMessage(message.toString());
        }
     
    }

    
}