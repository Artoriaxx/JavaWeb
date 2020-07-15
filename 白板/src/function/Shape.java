package function;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Shape implements Serializable {

    private int x1, y1, x2, y2, width;
    private Color color;
    private String type;
    private ImageIcon i;
    private transient JPanel dm;
    private String str;

    public Shape(int x1, int y1, int x2, int y2, int width, Color color, String type, ImageIcon i, JPanel dm, String str) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.width = width;
        this.color = color;
        this.type = type;
        this.i = i;
        this.dm = dm;
        this.str = str;
    }


    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(width));
        if (type.equals("直线") || type.equals("铅笔")  || type.equals("橡皮") || type.equals("喷枪")) {
            g.drawLine(x1, y1, x2, y2);
        }
        else if (type.equals("圆")) {
            g.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        }
        else if (type.equals("矩形")) {
            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        }
        else if (type.equals("圆角矩形")) {
            g.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1),
                    Math.abs(x2 - x1) / 3, Math.abs(y2 - y1) / 3);
        }
        else if (type.equals("实心矩形")) {
            g.fill3DRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), true);
        }
        else if (type.equals("Image")) {
            g.drawImage(i.getImage(), Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), dm);
        }
        else if (type.equals("文字")) {
            g.drawString(str, x1, y1);
        }
    }


}
