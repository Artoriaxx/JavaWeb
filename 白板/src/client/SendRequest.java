package client;

import function.Request;
import function.Response;

import java.io.IOException;

public class SendRequest {

    public static Response sendRequest(Request req) {
        Response res = null;
        try {
            Data.oos.writeObject(req);
            Data.oos.flush();
            if (!req.getType().equals(("logout"))) {
                res = (Response)Data.ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void sendRequest(Request req, int type) {
        try {
            Data.oos.writeObject(req);
            Data.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
