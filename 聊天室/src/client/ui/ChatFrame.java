package client.ui;

import client.*;
import function.Message;
import function.MyFile;
import function.Request;
import function.User;
import function.PopupListener;
import server.PackOfIO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;




public class ChatFrame extends JFrame implements ActionListener{

    private static Vector<Vector<User>> Group = new Vector<Vector<User>>();
    private static int groupCount = 0;
    private JScrollPane topScroll;

    private JLabel nameJL;
    public static JTextArea comArea;//公共群聊
    public static JTextArea myArea;//私人聊天
    private JComboBox<String> selectList; //下拉菜单
    private JTextField inField; //发言输入框
    private JCheckBox privateTalk;//私聊选择框

    private JButton sendJBT; //发送消息按钮
    private JList<String> peopleList; //显示进入聊天室的人名单
    private JButton refreshJBT;//刷新列表按钮
    private JButton SelectJBT;
    private JMenuItem menuItem;
    private JMenuItem hideMenu;
    private JMenuItem showMenu;
    private DefaultListModel<String> listModel;//用户列表
    private String myName = Data.me.getName();
    private String withWho = "所有人";
    private JButton sfb;
    private boolean pTlkFlag = false;
    ChatFrame() {
        super("网络聊天室");

        sfb = new JButton("选择文件");


        this.setSize(new Dimension(20, 20));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        //上方界面
        JPanel upperPanel = new JPanel();
        JLabel welcome = new JLabel();
        nameJL = new JLabel();
        nameJL.setText("欢迎，用户" + myName);
        upperPanel.add(welcome);
        upperPanel.add(nameJL);
        //中间界面
        JPanel hostAndPort = new JPanel(new BorderLayout());

        comArea = new JTextArea(15, 40);
        comArea.setEditable(false); //不可编辑
        comArea.getScrollableUnitIncrement(new Rectangle(10, 20), SwingConstants.VERTICAL, -2);
        topScroll = new JScrollPane(comArea);
        topScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        topScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        topScroll.setBorder(BorderFactory.createTitledBorder("公共群聊"));

        myArea = new JTextArea(10, 40);
        myArea.setEditable(false);
        myArea.setForeground(new Color(57, 77, 248));
        JScrollPane downScroll = new JScrollPane(myArea);
        downScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        downScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        downScroll.setBorder(BorderFactory.createTitledBorder("私人聊天"));

        hostAndPort.add(topScroll, BorderLayout.NORTH);
        hostAndPort.add(downScroll, BorderLayout.CENTER);
        //输入发送区
        JPanel centerLowerPanel = new JPanel(new BorderLayout());
        JPanel tmp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel tmp2 = new JPanel(new BorderLayout());
        JLabel with = new JLabel("发送给");
        selectList = new JComboBox<String>();
        selectList.addItem("所有人");//广播
        privateTalk = new JCheckBox("私聊");
        inField = new JTextField(33);
        inField.setBackground(new Color(200, 255, 255));
        inField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    sendMsg();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });//回车键发送
        sendJBT = new JButton("发送");
        sendJBT.setBackground(Color.WHITE);
        tmp1.add(sfb);//req
        tmp1.add(with);
        tmp1.add(selectList);
        tmp1.add(privateTalk);
        tmp2.add(inField, BorderLayout.CENTER);
        tmp2.add(sendJBT, BorderLayout.EAST);
        centerLowerPanel.add(tmp1, BorderLayout.CENTER);
        centerLowerPanel.add(tmp2, BorderLayout.SOUTH);
        hostAndPort.add(centerLowerPanel, BorderLayout.SOUTH);
        //进入房间的名单
        JPanel westPanel = new JPanel(new BorderLayout());
        Data.userListModel = new UserListModel(Data.userList);

        peopleList = new JList<>(Data.userListModel);
        peopleList.setCellRenderer(new MyCellRender());
        peopleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//设置为多选，可以选择多人
        JScrollPane ListScrollPane = new JScrollPane(peopleList);
        ListScrollPane.setPreferredSize(new Dimension(150, 400));
        refreshJBT = new JButton("刷新列表");
        SelectJBT = new JButton("选择发送人员");//选择多名群聊人员或者只选择一个
        westPanel.add(ListScrollPane, BorderLayout.CENTER);
        JPanel westLower = new JPanel(new BorderLayout());
        westLower.add(refreshJBT, BorderLayout.WEST);
        westLower.add(SelectJBT, BorderLayout.EAST);
        westPanel.add(westLower, BorderLayout.SOUTH);


        this.add(upperPanel, BorderLayout.NORTH);
        this.add(hostAndPort, BorderLayout.CENTER);
        this.add(westPanel, BorderLayout.WEST);


        sfb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        sendJBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });//监听发送按钮
        refreshJBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });//监听刷新按钮
        SelectJBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Object[] select = peopleList.getSelectedValues();
                    if (select != null) { //确保选择非空
                        if (select.length == 1) {
                            User u = (User)select[0];
                            if (u.getId() != Data.me.getId()) { //不能添加自己
                                int count = selectList.getItemCount();
                                for (int i = 0; i < count; i++) {
                                    selectList.setSelectedIndex(i);//选择第 I 项
                                    String strName = (String) selectList.getSelectedItem();
                                    String nowName = u.getName() + "(" + u.getId() + ")";
                                    if (nowName.equals(strName)) { //如果已经在列表中就复合框中选中所以要确保没有重名的哦
                                        return;
                                    }
                                }
                                //如果没有添加就会执行下面语句 添加
                                selectList.addItem(u.getName() + "(" + u.getId() + ")");//增加一个下拉项
                                selectList.setSelectedIndex(selectList.getItemCount() - 1);
                            }
                        } else {
                            Vector<User> tmp = new Vector<User>();
                            for (Object o : select) {
                                User u = (User)o;
                                if (u.getId() != Data.me.getId()) tmp.add((User)o);
                            }
                            Group.add(tmp);
                            groupCount = groupCount + 1;
                            selectList.addItem("--群组:" + groupCount);
                            selectList.setSelectedIndex(selectList.getItemCount() - 1);
                        }
                    }
                } catch (Exception ee) {
                    //System.out.println("发生错误 在 valueChanged " + ee);
                }
            }
        });
        selectList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withWho = (String) selectList.getSelectedItem();
            }
        });//监听下拉菜单
        privateTalk.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() == privateTalk) {
                    pTlkFlag = e.getStateChange() == ItemEvent.SELECTED;
                }
            }
        });//监听多选框状态
        this.createPopupMenu();
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() { //监听窗口关闭时间
            public void windowClosing(WindowEvent event) {
                shutDown();
            }
        });
        new ClientThread(this).start();
    }

    private void shutDown() {
        Request req = new Request();
        req.setType("logout");
        req.insertData("user", Data.me);
        SendRequest.sendRequest(req, 0);
        this.dispose();
        System.exit(0);
    }

    private void sendMsg() {
        try {
            String myWord = inField.getText();
            if (myWord.trim().length() == 0) return;
            Message msg = new Message();
            msg.setFrom(Data.me);
            if (pTlkFlag) msg.setType(1);//1为私聊
            else msg.setType(0);//0为公共聊天
            if (withWho.equals("所有人")) {
                appendText(comArea, myName + ": " + myWord + "\n");
                msg.setMessage(myName + ": " + myWord + "\n");
                msg.setType(0);
                msg.setTo(Data.userList);
            }
            else if (withWho.startsWith("--群组")) {
                int num = 0;
                for (int i = 5; i < withWho.length(); i++) num = num * 10 + withWho.charAt(i) - '0';

                if (pTlkFlag) {
                    myArea.append(myName + "发送给群组" + withWho.substring(5) + ": " + myWord + "\n");
                    myArea.append("群组成员:");
                    for (User u : Group.get(num - 1)) myArea.append(u.getName() + " ");
                    myArea.append("\n");
                    myArea.setCaretPosition(myArea.getText().length());
                    msg.setMessage(myName + ":   " + myWord + "\n");
                    List<User> tmp = new ArrayList<User>(Group.get(num - 1));
                    msg.setTo(tmp);
                }
                else {
                    String msgWord;
                    msgWord = myName + ": ";
                    for (User u : Group.get(num - 1)) msgWord = msgWord +  "@" + u.getName() + " ";
                    msgWord = msgWord + "\n" +  myWord + "\n";;
                    appendText(comArea, msgWord);
                    msg.setMessage(msgWord);
                    msg.setTo(Data.userList);
                }
            }
            else {
                if (pTlkFlag) {
                    List<User> tmp = new ArrayList<User>();
                    for (User u : Data.userList) {
                        String nowName = u.getName() + "(" + u.getId() + ")";
                        if (nowName.equals(withWho)) {
                            tmp.add(u);
                            break;
                        }
                    }
                    msg.setTo(tmp);
                    System.out.println(withWho);
                    System.out.println(msg.getTo().get(0).getName());
                    appendText(myArea,  "你对 [" + withWho + "] 说: " + myWord + "\n");
                    msg.setMessage(myName + ":   " + myWord + "\n");
                }
                else {
                    msg.setTo(Data.userList);
                    appendText(comArea, myName + ": " + "@" + withWho + " " + myWord + "\n");
                    msg.setMessage(myName + ": " + "@" + withWho + " " + myWord + "\n");
                }
            }
            Request req = new Request();
            req.setType("chat");
            req.insertData("txtMsg", msg);
            SendRequest.sendRequest(req, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inField.setText("");
        }

    }

    public static void remove() {
        JOptionPane.showMessageDialog(null, "您已经被强制下线", "系统通知", JOptionPane.WARNING_MESSAGE);

        Request req = new Request();
        req.setType("logout");
        req.insertData("user", Data.me);

        SendRequest.sendRequest(req);
        System.exit(0);

    }
    public static void appendText(JTextArea area, String word) {
        area.append(word);
        area.setCaretPosition(area.getText().length());
    }

    private void sendFile() {
        if (withWho.equals("所有人") || withWho.startsWith("--群组")) {
            JOptionPane.showMessageDialog(ChatFrame.this, "不能给群发文件!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User selectUser = null;
        for (User u : Data.userList) {
            String nowName = u.getName() + "(" + u.getId() + ")";
            if (nowName.equals(withWho)) {
                selectUser = u;
                break;
            }
        }

        if(selectUser != null){
            if(Data.me.getId() == selectUser.getId()){
                JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送文件!",
                        "不能发送", JOptionPane.ERROR_MESSAGE);
            }else{
                JFileChooser jfc = new JFileChooser();
                if (jfc.showOpenDialog(ChatFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    MyFile sendFile = new MyFile();
                    sendFile.setFrom(Data.me);
                    sendFile.setTo(selectUser);
                    try {
                        sendFile.setSrcName(file.getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    Request request = new Request();
                    request.setType("toSendFile");
                    request.insertData("file", sendFile);
                    SendRequest.sendRequest(request, 0);

                    appendText(myArea, "【文件消息】向 "
                            + selectUser.getName() + "("
                            + selectUser.getId() + ") 发送文件 ["
                            + file.getName() + "]，等待对方接收...\n");
                }
            }
        }else{
            JOptionPane.showMessageDialog(ChatFrame.this, "不能给所有在线用户发送文件!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        menuItem = new JMenuItem("清空主聊天频道信息");
        menuItem.addActionListener(this);
        popup.add(menuItem);//鼠标右击显示
        hideMenu = new JMenuItem("隐藏公共频道");
        hideMenu.addActionListener(this);
        popup.add(hideMenu);
        showMenu = new JMenuItem("显示公共频道");
        showMenu.addActionListener(this);
        popup.add(showMenu);
        MouseListener popupListener = new PopupListener(popup);//PopupListener 继承MouseAdapter
        comArea.addMouseListener(popupListener);
        myArea.addMouseListener(popupListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItem) {//清空主聊天频道被选中
            comArea.setText("");
        }
        if (e.getSource() == hideMenu) {//隐藏公共群聊
            topScroll.setVisible(false);
            this.pack();
        }
        if (e.getSource() == showMenu) {//显示
            topScroll.setVisible(true);
            this.pack();
        }
    }
}
