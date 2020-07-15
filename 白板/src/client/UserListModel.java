package client;

import function.User;

import javax.swing.*;
import java.util.List;

public class UserListModel extends AbstractListModel {
    private static final long serialVersionUID = -5038355908202580297L;
    private List<User> users;
    public UserListModel(List<User> users) {
        this.users = users;
    }
    @Override
    public int getSize() {
        return users.size();
    }

    @Override
    public Object getElementAt(int index) {
        return users.get(index);
    }

    public void addElement(User u) {
        if (users.contains(u)) return;
        int index = users.size();
        users.add(u);
        fireIntervalAdded(this, index, index);
    }

    public boolean removeElement(User u) {
        int index = users.indexOf(u);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        return users.remove(u);
    }

}
