package client.ui;

import client.Data;
import client.SendRequest;
import function.Request;
import function.Response;
import function.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LoginFrame extends JFrame  implements ActionListener, ItemListener, KeyListener {
    private String hostName = "localhost";
    private int port = 2333;
    private JTextField nameJTF;
    private JRadioButton isBoy, isGirl;
    private String sex = "";
    private JTextField hostText;
    private JTextField portText;
    private JButton cancel;
    private JButton ok;
    private BufferedReader in;
    private PrintWriter out;


    public LoginFrame() {//构造方法
        super("登陆");
        Container con = this.getContentPane();
        con.setLayout(new BorderLayout());
        JLabel hostJL = new JLabel("地址");
        JLabel portJL = new JLabel("端口");
        hostText = new JTextField(10);
        hostText.setText(hostName);
        portText = new JTextField(4);
        portText.setText(Integer.toString(port));
        cancel = new JButton("退出");
        ok = new JButton("连接");
        JLabel nameJL = new JLabel("姓名");
        nameJTF = new JTextField(10);
        isBoy = new JRadioButton("男");
        isGirl = new JRadioButton("女");
        ButtonGroup sexGroup = new ButtonGroup();
        sexGroup.add(isBoy);
        sexGroup.add(isGirl);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfo.add(nameJL);
        userInfo.add(nameJTF);
        userInfo.add(isBoy);
        userInfo.add(isGirl);

        JPanel hostAndPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostAndPort.add(hostJL);
        hostAndPort.add(hostText);
        hostAndPort.add(portJL);
        hostAndPort.add(portText);

        JPanel upperPanel = new JPanel(new GridLayout(2, 1));
        upperPanel.add(userInfo);
        upperPanel.add(hostAndPort);

        JPanel option = new JPanel();
        JLabel spaceLabel = new JLabel("");
        spaceLabel.setPreferredSize(new Dimension(20, 20));
        option.add(cancel);
        option.add(spaceLabel);
        option.add(ok);

        con.add(upperPanel, BorderLayout.CENTER);
        con.add(option, BorderLayout.SOUTH);


        isBoy.addItemListener(this);
        isGirl.addItemListener(this);
        isBoy.addKeyListener(this);
        isGirl.addKeyListener(this);
        hostText.addKeyListener(this);
        portText.addKeyListener(this);
        nameJTF.addKeyListener(this);
        cancel.addActionListener(this);
        ok.addActionListener(this);


        this.setPreferredSize(new Dimension(250, 150));
        this.setMaximumSize(new Dimension(250, 150));
        this.setLocation(500, 300);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == isBoy) sex = "Boy";
        if (e.getSource() == isGirl) sex = "Girl";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancel) {
            this.shutDown();
        }
        else if (e.getSource() == ok) {
            commit();
        }
    }

    private void commit() {
        if ((nameJTF.getText()).trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "请输入一个名字", "提示 ", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (sex.length() == 0) {
            JOptionPane.showMessageDialog(this, "请选择性别", "提示 ", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            try {
                User user = new User(nameJTF.getText(), sex);
                Request req = new Request();
                req.setType("login");
                req.insertData("user", user);
                Response res = SendRequest.sendRequest(req);
                user = (User)res.getData("user");
                Data.me = user;
                Data.userList = (List<User>)res.getData("userList");

                new ChatFrame();
                ok.setEnabled(false);
                this.dispose();
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(this, "登陆失败", "失败 ", JOptionPane.ERROR_MESSAGE);

            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {//监听回车
        if (e.getKeyCode() == 10) {
            commit();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void shutDown() {
        Request req = new Request();
        req.setType("logout");
        SendRequest.sendRequest(req, 0);
        try {
            Data.s.close();
            Data.ois.close();
            Data.oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
