package server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    final String token;
    String name;
    String chatRoom;
    Queue<String> messages = new LinkedList<String>();

    public User(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        String name = this.name;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatRoom() {
        String chatRoom = this.chatRoom;
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public String getMessage() {
        String message = messages.poll();
        return message;
    }
}