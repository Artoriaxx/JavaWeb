package function;

import server.RequestProcess;
import server.ui.ServerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawListener extends MouseAdapter implements ActionListener {
    private String type = "Line";// 图形类型
    private Color color = Color.black;// 颜色
    private int width = 1;// 粗细

    private int x1, y1, x2, y2;// 坐标值
    private Graphics2D g;// 画笔
    private JPanel canvas;// 画布
    public static List<Shape> list; //存储画过的形状
    private ImageIcon i = null;
    public static String drawText;

    public DrawListener(JPanel canvas) {
        this.canvas = canvas;
        list = new ArrayList<Shape>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = e.getActionCommand();
        if (text.equals("选择颜色")) {
            Color co = JColorChooser.showDialog(canvas, "选取颜色", null);
            if (co == null) return;
            else {
                color = co;
                ServerFrame.colorShow.setBackground(co);
            }
        }
        else if (Character.isDigit(text.charAt(0))) {
            width = Integer.parseInt(text);
        }
        else if (text.equals("文字")) {
            type = text;
            drawText = JOptionPane.showInputDialog("请输入");
        }
        else {
            type = text;
            System.out.println(type);
        }
    }


    public void mouseClicked(MouseEvent e) {
    }


    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        g = (Graphics2D) canvas.getGraphics();// 从窗体上获取画笔对象
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 抗锯齿
        g.setColor(color);// 设置画笔颜色
        g.setStroke(new BasicStroke(width));// 设置画笔线条粗细

    }


    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        Shape s = new Shape(x1, y1, x2, y2, width, color, type, i, canvas, drawText);
        s.draw(g);
        list.add(s);
        try {
            RequestProcess.sendShape(s);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (type.equals("铅笔")) {
            x2 = e.getX();
            y2 = e.getY();
            Shape s = new Shape(x1, y1, x2, y2, width, color, type, i, canvas, drawText);
            s.draw(g);
            list.add(s);
            try {
                RequestProcess.sendShape(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            x1 = x2;
            y1 = y2;
        }
        else if (type.equals("橡皮")) {
            x2 = e.getX();
            y2 = e.getY();
            Shape s = new Shape(x1, y1, x2, y2, width, Color.WHITE, type, i, canvas, drawText);
            s.draw(g);
            list.add(s);
            try {
                RequestProcess.sendShape(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            x1 = x2;
            y1 = y2;
        }
        else if (type.equals("喷枪")) {
            Random rand = new Random();
            int size = rand.nextInt(50);// 随机决定要画的点数
            x2 = e.getX();
            y2 = e.getY();
            for (int j = 0; j < size; j++) {
                // 在0-7之间可以取50次
                int x = rand.nextInt(10);
                int y = rand.nextInt(10);
                // 不断改变（x1,y1）的坐标值，实现在(x1,y1)的周围画点
                Shape s = new Shape(x2 + x, y2 + y, x2 + x, y2 + y, width, color, type, i, canvas, drawText);
                s.draw(g);
                list.add(s);
                try {
                    RequestProcess.sendShape(s);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                x1 = x2;
                y1 = y2;
            }
        }
        else if (!type.equals("文字")) {//使用图层方法来画图，这样可以做到动态
            x2 = e.getX();
            y2 = e.getY();
            Image iBuffer = canvas.createImage(canvas.getWidth(), canvas.getHeight());
            Graphics gbuffer = iBuffer.getGraphics();
            gbuffer.setColor(Color.white);
            gbuffer.fill3DRect(0, 0, canvas.getWidth(), canvas.getHeight(), true);

            for (int i = 0; i < list.size(); i++) {
                Shape shape = list.get(i);
                shape.draw((Graphics2D) gbuffer);
            }

            gbuffer.setColor(this.color);

            Shape tmp = new Shape(x1, y1, x2, y2, width, color, type, i, canvas, drawText);
            tmp.draw((Graphics2D) gbuffer);
            Shape s = new Shape(0,0,canvas.getWidth(),canvas.getHeight(),canvas.getWidth(),color,"Image",new ImageIcon(iBuffer),canvas, drawText);
            s.draw(g);
            try {
                RequestProcess.sendShape(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        }
    }
}
