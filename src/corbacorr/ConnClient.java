package corbacorr;

import conn.Conn;
import conn.ConnHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class ConnClient
{
    static final String CMD_PREFIX = "/";
    static final String CMD_CREATE = CMD_PREFIX + "create ";
    static final String CMD_LIST   = CMD_PREFIX + "list";
    static final String CMD_JOIN   = CMD_PREFIX + "join ";

    static Conn connImpl;
    static String token;

    public static class Input implements Runnable {

        public void run() {
            Scanner in = new Scanner(System.in);
            while (true) {
                String s = in.nextLine();
                parse(s);
            }
        }

        void parse(String str) {
            if (str.startsWith(CMD_CREATE)) {
                String name = str.substring(CMD_CREATE.length());
                connImpl.createChatRoom(token, name);
            } else if (str.startsWith(CMD_LIST)) {
                connImpl.listChatRooms(token);
            } else if (str.startsWith(CMD_JOIN)) {
                String name = str.substring(CMD_JOIN.length());
                connImpl.joinChatRoom(token, name);
            } else {
                connImpl.sendMessage(token, str);
            }
        }
    }

    public static class Output implements Runnable {

        public void run() {
            while(true) {
                String message = connImpl.receiveMessage(token);
                if (!message.isEmpty()) {
                    System.out.println(message);
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String args[])
    {
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "Conn";
            connImpl = ConnHelper.narrow(ncRef.resolve_str(name));

            System.out.println("Obtained a handle on server object: " + connImpl);
            token = connImpl.connect();

            new Thread(new Input()).start();
            new Thread(new Output()).start();

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

}