package client;

import function.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Data {
    public static User me;
    public static List<User> userList;
    public static Socket s;
    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;
    public static int port = 2333;
    public static String localIp;
    public static String serverIp = "localhost";
    public static int fileport = 6666;

    public static UserListModel userListModel;

    static {
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
