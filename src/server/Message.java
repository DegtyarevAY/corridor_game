package server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    String username;
    String text;

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public String toString() {
        return text;
    }
}