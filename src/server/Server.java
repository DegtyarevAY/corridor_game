package server;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    static final String DEFAULT_NAME = "Anonymous";

    HashMap<String, User> users = new HashMap<String, User>();
    HashMap<String, ChatRoom> chatRooms = new HashMap<String, ChatRoom>();
    SecureRandom random = new SecureRandom();

    public void addChatRoom(final String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRooms.put(chatRoom.getName(), chatRoom);
    }

    public ChatRoom getChatRoom(String name) {
        ChatRoom chatRoom = chatRooms.get(name);
        return chatRoom;
    }

    public ArrayList<String> getChatRooms() {
        ArrayList<String> names = new ArrayList<String>(chatRooms.keySet());
        return names;
    }

    public String addUser() {
        String token = randomString();
        User user = new User(token, DEFAULT_NAME);
        users.put(user.getToken(), user);
        return token;
    }

    public User getUser(String token) {
        User user = users.get(token);
        return user;
    }

    String randomString() {
        return new BigInteger(130, random).toString(32);
    }
}