package client;

import client.ui.LoginFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client {
    public static void main(String[] args) {
        connection();
        new LoginFrame();
    }
    public static void connection() {
        String ip = Data.serverIp;
        int port = Data.port;
        try {
            Data.s = new Socket(ip, port);
            Data.oos = new ObjectOutputStream(Data.s.getOutputStream());
            Data.ois = new ObjectInputStream(Data.s.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
