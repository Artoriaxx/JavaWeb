package server;

import function.User;

import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    public static ServerSocket ss;
    public static Map<Integer, PackOfIO> IOMap;
    public static Map<Integer, User> UserMap;
    public static int port = 2333;
    public static String ip = "localhost";
    public static UserTableModel userTableModel;

    static {
        IOMap = new ConcurrentHashMap<Integer, PackOfIO>();
        UserMap = new ConcurrentHashMap<Integer, User>();
        userTableModel = new UserTableModel();
    }
}
