package conn;


/**
* conn/ConnOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Conn.idl
* 4 ������� 2018 �. 10:28:14 MSK
*/

public interface ConnOperations 
{
  String connect ();
  void sendMessage (String token, String message);
  String receiveMessage (String token);
  void createChatRoom (String token, String name);
  void listChatRooms (String token);
  void joinChatRoom (String token, String name);
  void leaveChatRoom (String token);
  void changeName (String token, String name);
} // interface ConnOperations