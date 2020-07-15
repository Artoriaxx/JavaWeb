package server.ui;

import function.Message;
import function.PopupListener;
import function.Response;
import function.User;
import server.Data;
import server.PackOfIO;
import server.RequestProcess;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class ServerFrame extends JFrame {
    private JTextField inField;
    private JTable userTable;

    public ServerFrame() {
        this.setTitle("服务器");
        this.setSize(new Dimension(700, 475));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        northPanel.setBorder(BorderFactory.createTitledBorder(border, "服务器信息", TitledBorder.LEFT,TitledBorder.TOP));
        this.add(northPanel, BorderLayout.NORTH);
        JLabel serverPort = new JLabel("服务器端口: " + Data.port);
        northPanel.add(serverPort, BorderLayout.WEST);
        JLabel serverIP = new JLabel("服务器IP: " + Data.ip);
        northPanel.add(serverIP, BorderLayout.EAST);

        userTable = new JTable(Data.userTableModel);
        this.createPopupMenu();

        JTabbedPane centerPanel = new JTabbedPane();
        centerPanel.addTab("在线用户列表", new JScrollPane(userTable));
        centerPanel.setTabComponentAt(0, new JLabel("在线用户列表"));
        this.add(centerPanel, BorderLayout.CENTER);

        inField = new JTextField(40);
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
        });
        JButton send = new JButton("发送系统信息");
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel sendPanel = new JPanel();
        sendPanel.add(inField);
        sendPanel.add(send);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendAll();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        southPanel.add(sendPanel, BorderLayout.NORTH);
        final JLabel time = new JLabel("", SwingConstants.RIGHT);
        time.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        //用定时任务来显示当前时间
        new java.util.Timer().scheduleAtFixedRate(
            new TimerTask(){
                DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                public void run() {
                    time.setText("当前时间：" + df.format(new Date()) + "  ");
                }
            }, 0, 1000);
        southPanel.add(time, BorderLayout.SOUTH);
        this.add(southPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void sendAll() throws IOException {

        String word = inField.getText();
        if (word.trim().length() == 0) return;
        Message msg = new Message();
        msg.setType(0);
        msg.setMessage("【系统通知】 " + word + "\n");
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
                    msg.setMessage("【系统消息】" + outMsg + "\n");
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
    public static void main(String[] args) {
        new ServerFrame();
    }
}
