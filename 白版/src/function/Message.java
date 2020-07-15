package function;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 847185357566365072L;

    private List<User> to;
    private User from;
    private String message;
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
