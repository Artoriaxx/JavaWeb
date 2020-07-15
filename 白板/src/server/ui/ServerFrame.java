package server.ui;

import function.*;
import server.Data;
import server.PackOfIO;
import server.RequestProcess;
import server.UserDB;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class ServerFrame extends JFrame {
    private JTextField inField;
    private JTable userTable;
    private JPanel canvas;
    public static JLabel colorShow;
    public static JTextArea comArea;

    public ServerFrame() {
        this.setTitle("教师端");
        this.setSize(new Dimension(1500, 800));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(new Color(255,255,255));
        this.setLayout(new BorderLayout());
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        northPanel.setBorder(BorderFactory.createEtchedBorder(new Color(2, 2, 2), new Color(57, 77, 248)));
        northPanel.setBackground(new Color(255, 255, 255));
        northPanel.setPreferredSize(new Dimension(1500, 150));
        this.add(northPanel, BorderLayout.NORTH);

        JPanel shapePanel = myJPanel(400, 150);
        shapePanel.setLayout(new BorderLayout());
        JPanel shapeBtnPanel = new JPanel();
        shapeBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 15));
        shapeBtnPanel.setOpaque(false);
        shapeBtnPanel.setPreferredSize(new Dimension(350, 100));
        shapePanel.add(shapeBtnPanel, BorderLayout.SOUTH);
        JLabel shape1 = myJLabel("图形");
        shape1.setPreferredSize(new Dimension(340, 50));
        shapePanel.add(shape1, BorderLayout.NORTH);
        northPanel.add(shapePanel);

        String[] btnName = {"直线", "圆", "矩形", "圆角矩形", "实心矩形", "铅笔", "喷枪", "文字", "橡皮"};

        canvas = myJPanel(1000, 500);
        this.add(canvas, BorderLayout.CENTER);
        DrawListener dl = new DrawListener(canvas);
        canvas.addMouseListener(dl);
        canvas.addMouseMotionListener(dl);

        for (int i = 0; i < btnName.length; i++) {
            JButton jb = new JButton(btnName[i]);
            shapeBtnPanel.add(jb);
            jb.addActionListener(dl);
        }



        JPanel colorPanel = myJPanel(100, 150);
        northPanel.add(colorPanel);
        JLabel color1 = myJLabel("选择颜色");
        color1.setPreferredSize(new Dimension(90, 50));
        colorPanel.setLayout(new BorderLayout());
        colorPanel.add(color1, BorderLayout.NORTH);

        JPanel colorChoosePanel = new JPanel();
        colorChoosePanel.setLayout(new FlowLayout());
        colorChoosePanel.setPreferredSize(new Dimension(100, 100));
        colorChoosePanel.setBackground(Color.WHITE);
        colorShow = new JLabel();
        colorShow.setOpaque(true);
        colorShow.setPreferredSize(new Dimension(40, 40));
        colorShow.setBackground(Color.BLACK);
        JButton chooseColor = new JButton("选择颜色");
        chooseColor.addActionListener(dl);
        colorChoosePanel.add(colorShow);
        colorChoosePanel.add(chooseColor);

        colorPanel.add(colorChoosePanel, BorderLayout.SOUTH);


        JPanel strokePanel = myJPanel(150, 150);
        JPanel strokeChoosePanel = new JPanel();
        strokeChoosePanel.setPreferredSize(new Dimension(140, 90));
        strokeChoosePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        strokeChoosePanel.setBackground(Color.WHITE);
        JLabel stroke1 = myJLabel("粗细");
        stroke1.setPreferredSize(new Dimension(140, 50));
        strokePanel.add(stroke1, BorderLayout.NORTH);
        strokePanel.add(strokeChoosePanel, BorderLayout.SOUTH);
        String[] strokeName = {"1", "2", "3", "4", "5", "6"};
        for (int i = 0; i < strokeName.length; i++) {
            JButton jb = new JButton(strokeName[i]);
            strokeChoosePanel.add(jb);
            jb.addActionListener(dl);
        }
        northPanel.add(strokePanel);


        userTable = new JTable(Data.userTableModel);
        this.createPopupMenu();

        JTabbedPane listPanel = new JTabbedPane();
        listPanel.addTab("在线用户列表", new JScrollPane(userTable));
        listPanel.setTabComponentAt(0, new JLabel("在线用户列表"));
        listPanel.setPreferredSize(new Dimension(500, 150));
        listPanel.setBackground(Color.WHITE);
        northPanel.add(listPanel);



        JPanel msgPanel = myJPanel(331, 150);
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
        JLabel welcome = myJLabel("教师端，欢迎您");
        welcome.setPreferredSize(new Dimension(400, 50));
        msgPanel.add(welcome, BorderLayout.NORTH);
        msgPanel.add(time, BorderLayout.SOUTH);
        northPanel.add(msgPanel);





        JPanel eastPanel = myJPanel(333, 620);
        eastPanel.setLayout(new BorderLayout());

        comArea = new JTextArea(33, 50);
        comArea.setEditable(false); //不可编辑
        comArea.getScrollableUnitIncrement(new Rectangle(10, 20), SwingConstants.VERTICAL, -2);
        JScrollPane topScroll = new JScrollPane(comArea);
        topScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        topScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        topScroll.setBorder(BorderFactory.createTitledBorder("讨论区"));

        JButton sfb = new JButton("选择文件");
        sfb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        eastPanel.add(topScroll, BorderLayout.NORTH);
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
        tmp1.add(sfb);//req
        tmp2.add(inField, BorderLayout.CENTER);
        tmp2.add(sendJBT, BorderLayout.EAST);
        centerLowerPanel.add(tmp1, BorderLayout.CENTER);
        centerLowerPanel.add(tmp2, BorderLayout.SOUTH);

        eastPanel.add(centerLowerPanel, BorderLayout.SOUTH);
        this.add(eastPanel, BorderLayout.EAST);

        this.setResizable(false);
        this.setVisible(true);
    }
    public static MyFile sf;
    private void sendFile() {
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(ServerFrame.this) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            for (Integer id : Data.IOMap.keySet()) {
                User from = UserDB.user;
                sf = new MyFile();
                sf.setFrom(from);
                User now = Data.UserMap.get(id);
                sf.setTo(now);
                try {
                    sf.setSrcName(file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Request req = new Request();
                req.setType("toSendFile");
                req.insertData("file", sf);
                RequestProcess.toSendFile(req);
                comArea.append("【文件消息】向 " + now.getName() + "(" + now.getId() + ") 发送文件 [" + file.getName() + "]，等待对方接收...\n");
                comArea.setCaretPosition(comArea.getText().length());
            }
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
        msg.setType(0);
        msg.setMessage("【教师信息】 " + word + "\n");
        comArea.append("【教师信息】 " + word + "\n");
        comArea.setCaretPosition(comArea.getText().length());
        Response res = new Response();
        res.setType("chat");
        res.insertData("txtMsg", msg);
        RequestProcess.sendAll(res);
        inField.setText("");
    }


    private void createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem send = new JMenuItem("发送信息");
        send.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = userTable.getSelectedRow();
                if (index < 0) return;
                String uid = (String)userTable.getValueAt(index, 0);
                sendOne(Integer.valueOf(uid));
            }


        });
        popup.add(send);//鼠标右击显示
        JMenuItem remove = new JMenuItem("强制下线");
        remove.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                int index = userTable.getSelectedRow();
                String uid = (String)userTable.getValueAt(index, 0);
                remove(Integer.valueOf(uid));
            }
        });
        popup.add(remove);

        MouseListener popupListener = new PopupListener(popup);//PopupListener 继承MouseAdapter
        userTable.addMouseListener(popupListener);
    }
    private void sendOne(Integer id) {
        System.out.println(151);
        JDialog word = new JDialog(this, true);
        word.setLayout(new FlowLayout());
        word.setSize(200,100);
        JTextField jtf = new JTextField(15);
        JButton send = new JButton("发送");
        word.add(jtf);
        word.add(send);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String outMsg = jtf.getText();
                if (outMsg.trim().length() == 0) {
                    JOptionPane.showMessageDialog(null, "输入不能为空", "输入不能为空", JOptionPane.WARNING_MESSAGE);
                }
                try {
                    Message msg = new Message();
                    msg.setType(0);
                    msg.setMessage("【教师信息】" + outMsg + "\n");
                    Response res = new Response();
                    res.setType("chat");
                    res.insertData("txtMsg", msg);
                    RequestProcess.sendResponse(Data.IOMap.get(id), res);
                } finally {
                    jtf.setText("");
                    word.dispose();
                }
            }
        });
        word.setVisible(true);
    }
    private void remove(Integer uid) {
        Message msg = new Message();
        msg.setType(0);
        msg.setMessage("【系统通知】 您被强制下线\n");
        Response res = new Response();
        res.setType("remove");
        res.insertData("txtMsg", msg);
        RequestProcess.sendResponse(Data.IOMap.get(uid), res);
        SwingUtilities.updateComponentTreeUI(userTable);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D gg = (Graphics2D)g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < DrawListener.list.size(); i++) {
            DrawListener.list.get(i).draw((Graphics2D)canvas.getGraphics());
        }

    }

    public static void main(String[] args) {
        new ServerFrame();
    }
}
