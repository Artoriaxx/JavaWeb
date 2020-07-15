package server;


import function.*;
import server.ui.ServerFrame;

import javax.print.attribute.standard.Severity;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class RequestProcess implements Runnable {
    private Socket s;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    public RequestProcess(Socket s) {
        try {
            this.s = s;
            this.ois = new ObjectInputStream(s.getInputStream());
            this.oos = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        boolean flag = true;
        try {
            PackOfIO nowIO = new PackOfIO(ois, oos);
            while (flag) {
                Request req = (Request) nowIO.getOis().readObject();
                String type = req.getType();
                if (type.equals("login")) {
                    login(nowIO, req);
                }
                else if (type.equals("logout")) {
                    flag = false;
                    logout(nowIO, req);
                }
                else if (type.equals("chat")) {
                    chat(req);
                }
                else if (type.equals("toSendFile")) {
                    toSendFile(req);
                }
                else if (type.equals("agree")) {
                    agree(req);
                }
                else if (type.equals("refuse")) {
                    refuse(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agree(Request req) {
        MyFile sf = (MyFile)req.getData("sendFile");

        //向接收方发出接收文件的响应
        Response response2 = new Response();
        response2.setType("receive");
        response2.insertData("sendFile", sf);
        PackOfIO receiveIO = Data.IOMap.get(sf.getTo().getId());
        this.sendResponse(receiveIO, response2);

        sendFile(sf);
    }
    public void sendFile(MyFile sf) {
        try {
            Socket s = new Socket(sf.getTargetIp(),sf.getTargetPort());//套接字连接
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sf.getSrcName()));//文件读入
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());//文件写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                ServerFrame.comArea.append("【文件消息】文件发送完毕!\n");
                ServerFrame.comArea.setCaretPosition(ServerFrame.comArea.getText().length());
            }
            s.close();
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void toSendFile(Request req) {
        Response response = new Response();
        response.setType("toSendFile");
        MyFile sendFile = (MyFile)req.getData("file");
        response.insertData("sendFile", sendFile);
        //给文件接收方转发文件发送方的请求
        PackOfIO IO = Data.IOMap.get(sendFile.getTo().getId());
        sendResponse(IO, response);
    }

    private void refuse(Request req) {
        MyFile sendFile = (MyFile)req.getData("sendFile");
        ServerFrame.comArea.append(sendFile.getTo().getName() + "已经拒收文件\n");
        ServerFrame.comArea.setCaretPosition(ServerFrame.comArea.getText().length());
    }

    private void chat(Request req) {
        Message msg = (Message)req.getData("txtMsg");
        Response res = new Response();
        res.setType("chat");
        res.insertData("txtMsg", msg);

        for (User u : msg.getTo()) {
            if (u.getId() == msg.getFrom().getId()) continue;
            else {
                System.out.println(msg.getMessage());
                sendResponse(Data.IOMap.get(u.getId()), res);
            }
        }
        ServerFrame.comArea.append(msg.getMessage());
        ServerFrame.comArea.setCaretPosition(ServerFrame.comArea.getText().length());

    }

    private void logout(PackOfIO IO, Request req) throws IOException {
        User user = (User)req.getData("user");
        if (user == null) {
            s.close();
            oos.close();
            ois.close();
            return;
        }
        Data.IOMap.remove(user.getId());
        Data.UserMap.remove(user.getId());
        Response res = new Response();
        res.setType("logout");
        res.insertData("logoutUser", user);
        Data.userTableModel.removeElement(user.getId());
        sendAll(res);
        IO.getOos().close();
        IO.getOos().close();
        s.close();
    }

    private void login(PackOfIO IO, Request req) throws IOException {
        User user = (User)req.getData("user");
        user.setId(++UserDB.idCnt);
        Data.UserMap.put(user.getId(), user);
        Response res = new Response();
        res.insertData("user", user);
        res.insertData("userList", new ArrayList<User>(Data.UserMap.values()));
        IO.getOos().writeObject(res);
        IO.getOos().flush();

        Response res2 = new Response();
        res2.setType("login");
        res2.insertData("loginUser", user);
        sendAll(res2);
        Data.IOMap.put(user.getId(), IO);
        String[] newUser = {String.valueOf(user.getId()), user.getName(), user.getSex()};
        Data.userTableModel.addElement(newUser);
    }

    public static void sendResponse(PackOfIO IO, Response r) {
        try {
            IO.getOos().writeObject(r);
            IO.getOos().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAll(Response res2) throws IOException {
        for (PackOfIO IO : Data.IOMap.values()) {
            sendResponse(IO, res2);
        }
    }
    public static void sendShape(Shape s) throws IOException {
        Response res = new Response();
        res.setType("shape");
        res.insertData("shape", s);
        sendAll(res);
    }
}
