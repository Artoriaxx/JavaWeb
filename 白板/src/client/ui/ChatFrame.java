package client.ui;

import client.*;
import function.*;
import client.Data;

import function.Shape;
import server.ui.ServerFrame;

import javax.print.DocFlavor;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class ChatFrame extends JFrame {
    private JTextField inField;
    private JPanel canvas;
    public static JTextArea comArea;

    public ChatFrame() {
        this.setTitle("学生端");
        this.setSize(new Dimension(1500, 615));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());


        Data.userListModel = new UserListModel(Data.userList);

        canvas = myJPanel(1000, 500);
        this.add(canvas, BorderLayout.CENTER);
        DrawListener dl = new DrawListener(canvas);
        canvas.addMouseListener(dl);
        canvas.addMouseMotionListener(dl);


        JPanel eastPanel = myJPanel(333, 620);
        eastPanel.setLayout(new BorderLayout());

        JPanel msgPanel = myJPanel(331, 100);
        msgPanel.setLayout(new BorderLayout());
        final JLabel time = new JLabel("", SwingConstants.RIGHT);
        time.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        time.setHorizontalAlignment(JLabel.CENTER);
        //用定时任务来显示当前时间
        new java.util.Timer().scheduleAtFixedRate(
                new TimerTask(){
                    DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    public void run() {
                        time.setText("当前时间：" + df.format(new Date()) + "  ");
                    }
                }, 0, 1000);
        time.setPreferredSize(new Dimension(400, 50));
        JLabel welcome = myJLabel("学生，" + Data.me.getName() + "欢迎您");
        welcome.setPreferredSize(new Dimension(400, 50));
        msgPanel.add(welcome, BorderLayout.NORTH);
        msgPanel.add(time, BorderLayout.SOUTH);
        eastPanel.add(msgPanel, BorderLayout.NORTH);

        comArea = new JTextArea(33, 50);
        comArea.setEditable(false); //不可编辑
        comArea.getScrollableUnitIncrement(new Rectangle(10, 20), SwingConstants.VERTICAL, -2);
        JScrollPane topScroll = new JScrollPane(comArea);
        topScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        topScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        topScroll.setBorder(BorderFactory.createTitledBorder("讨论区"));


        eastPanel.add(topScroll, BorderLayout.CENTER);
        JPanel centerLowerPanel = new JPanel(new BorderLayout());
        JPanel tmp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel tmp2 = new JPanel(new BorderLayout());
        inField = new JTextField(33);
        inField.setBackground(new Color(200, 255, 255));
        inField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    try {
                        sendAll();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });//回车键发送
        JButton sendJBT = new JButton("发送");
        sendJBT.setBackground(Color.WHITE);
        sendJBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendAll();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        tmp2.add(inField, BorderLayout.CENTER);
        tmp2.add(sendJBT, BorderLayout.EAST);
        centerLowerPanel.add(tmp1, BorderLayout.CENTER);
        centerLowerPanel.add(tmp2, BorderLayout.SOUTH);

        eastPanel.add(centerLowerPanel, BorderLayout.SOUTH);
        this.add(eastPanel, BorderLayout.EAST);


        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                Request req = new Request();
                req.setType("logout");
                req.insertData("user", client.Data.me);

                SendRequest.sendRequest(req);
            }
        });

        this.setResizable(false);
        this.setVisible(true);

        new ClientThread(this).start();
    }
    public static MyFile sf;
    public static List<Shape> list = new ArrayList<Shape>();
    public static void draw(Response res, JFrame jf) {
        Shape shape = (Shape)res.getData("shape");
        shape.draw((Graphics2D)jf.getGraphics());
        list.add(shape);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D gg = (Graphics2D)g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < list.size(); i++) {
            Shape shape = list.get(i);
            shape.draw(gg);
        }
    }

    private JPanel myJPanel(int width, int height) {
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEtchedBorder(new Color(255, 255, 255), new Color(57, 77, 248)));
        jp.setPreferredSize(new Dimension(width, height));
        jp.setBackground(new Color(255,255,255));
        return jp;
    }
    private JLabel myJLabel(String s) {
        JLabel jl = new JLabel(s);
        jl.setHorizontalAlignment(JLabel.CENTER);
        jl.setFont(new Font("微软雅黑", Font.BOLD, 15));
        return jl;
    }
    private void sendAll() throws IOException {

        String word = inField.getText();
        if (word.trim().length() == 0) return;
        Message msg = new Message();
        appendText(comArea, Data.me.getName() + ": " + word + "\n");
        msg.setMessage(Data.me.getName() + ": " + word + "\n");
        msg.setType(0);
        msg.setTo(Data.userList);
        msg.setFrom(Data.me);
        for (User u : Data.userList) {
            System.out.println("184 " + u.getName() + "id:" + u.getId());
        }
        Request req = new Request();
        req.setType("chat");
        req.insertData("txtMsg", msg);
        SendRequest.sendRequest(req, 0);
        inField.setText("");
    }

    public static void remove() {
        JOptionPane.showMessageDialog(null, "您已经被强制下线", "系统通知", JOptionPane.WARNING_MESSAGE);

        Request req = new Request();
        req.setType("logout");
        req.insertData("user", client.Data.me);

        SendRequest.sendRequest(req);
        System.exit(0);

    }

    public static void appendText(JTextArea area, String word) {
        area.append(word);
        area.setCaretPosition(area.getText().length());
    }

    public static void main(String[] args) {
        new ChatFrame();
    }

}
