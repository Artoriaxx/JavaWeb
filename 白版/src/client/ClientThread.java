package client;

import client.ui.ChatFrame;
import function.*;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread extends Thread {
    private JFrame currentFrame;  //当前窗体

    public ClientThread(JFrame frame){
        currentFrame = frame;
    }
    public void run() {
        try {
            while (Data.s.isConnected()) {
                Response response = (Response)Data.ois.readObject();
                String type = response.getType();

                System.out.println(type);
                if (type.equals("login")) {
                    User newUser = (User)response.getData("loginUser");
                    Data.userListModel.addElement(newUser);
                    ChatFrame.appendText(ChatFrame.comArea, "【系统消息】用户" + newUser.getName() + "上线了！\n");
                }
                else if(type.equals("logout")) {
                    User newUser = (User)response.getData("logoutUser");
                    Data.userListModel.removeElement(newUser);
                    ChatFrame.appendText(ChatFrame.comArea, "【系统消息】用户" + newUser.getName() + "下线了！\n");

                }
                else if(type.equals("chat")) { //聊天
                    Message msg = (Message)response.getData("txtMsg");
                    ChatFrame.appendText(ChatFrame.comArea, msg.getMessage());

                }
                else if(type.equals("toSendFile")) { //准备发送文件
                    toSendFile(response);
                }
                else if(type.equals("agree")){ //对方同意接收文件
                    sendFile(response);
                }
                else if(type.equals("refuse")){ //对方拒绝接收文件
                    ChatFrame.appendText(ChatFrame.comArea, "【文件消息】对方拒绝接收，文件发送失败！\n");
                }
                else if(type.equals("receive")) { //开始接收文件
                    receiveFile(response);
                }
                else if(type.equals("remove")) {
                    Message msg = (Message)response.getData("txtMsg");
                    if (msg.getType() == 1) ChatFrame.appendText(ChatFrame.comArea, msg.getMessage());
                    else ChatFrame.appendText(ChatFrame.comArea, msg.getMessage());
                    ChatFrame.remove();
                }
                else if (type.equals("shape")) {
                    ChatFrame.draw(response, currentFrame);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //发送文件
    private void sendFile(Response response) {
        final MyFile sf = (MyFile) response.getData("sendFile");

        try {
            Socket s = new Socket(sf.getTargetIp(), sf.getTargetPort());//套接字连接
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sf.getSrcName()));//文件读入
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());//文件写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();

            synchronized (this) {
                ChatFrame.appendText(ChatFrame.comArea, "【文件消息】文件发送完毕!\n");
            }
            bis.close();
            bos.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收文件
    private void receiveFile(Response response) {
        final MyFile sendFile = (MyFile)response.getData("sendFile");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ServerSocket serverSocket = null;
        Socket s = null;
        try {
            serverSocket = new ServerSocket(sendFile.getTargetPort());
            s = serverSocket.accept(); //接收
            bis = new BufferedInputStream(s.getInputStream());//缓冲读
            bos = new BufferedOutputStream(new FileOutputStream(sendFile.getTargetName()));//缓冲写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                ChatFrame.appendText(ChatFrame.comArea, "【文件消息】文件接收完毕!存放在[" + sendFile.getTargetName()+"]\n");
            }
            bis.close();
            bos.close();
            s.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //准备发送文件
    private void toSendFile(Response response) {
        MyFile sendFile = (MyFile)response.getData("sendFile");
        String fromName = sendFile.getFrom().getName() + "(" + sendFile.getFrom().getId() + ")";
        String fileName = sendFile.getSrcName().substring(sendFile.getSrcName().lastIndexOf(File.separator)+1);
        int select = JOptionPane.showConfirmDialog(this.currentFrame, fromName + " 向您发送文件 [" + fileName+ "]!\n同意接收吗?", "接收文件", JOptionPane.YES_NO_OPTION);
        try {
            Request res = new Request();
            res.insertData("sendFile", sendFile);

            if (select == JOptionPane.YES_OPTION) {
                JFileChooser jfc = new JFileChooser();
                jfc.setSelectedFile(new File(fileName));
                int result = jfc.showSaveDialog(this.currentFrame);

                if (result == JFileChooser.APPROVE_OPTION){
                    //设置目的地文件名
                    sendFile.setTargetName(jfc.getSelectedFile().getCanonicalPath());
                    //设置目标地的IP和接收文件的端口
                    sendFile.setTargetIp(Data.localIp);
                    System.out.println(sendFile.getTargetPort());
                    sendFile.setTargetPort(Data.fileport);

                    res.setType("agree");
                    ChatFrame.appendText(ChatFrame.comArea, "【文件消息】您已同意接收来自 " + fromName +" 的文件，正在接收文件 ...\n");
                }
                else {
                    res.setType("refuse");
                    ChatFrame.appendText(ChatFrame.comArea, "【文件消息】您已拒绝接收来自 " + fromName +" 的文件!\n");
                }
            }
            else {
                res.setType("refuse");
                ChatFrame.appendText(ChatFrame.comArea, "【文件消息】您已拒绝接收来自 "
                        + fromName +" 的文件!\n");
            }

            SendRequest.sendRequest(res, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
