package client;

import function.User;

import javax.swing.*;
import java.awt.*;

public class MyCellRender extends JLabel implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User user = (User)value;
        String name = user.getName() + "(" + user.getId() + ")" + "[" + user.getSex() + "]";
        setText(name);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
