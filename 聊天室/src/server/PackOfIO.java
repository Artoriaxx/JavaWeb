package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class PackOfIO {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public PackOfIO(ObjectInputStream ois, ObjectOutputStream oos) {
        this.ois = ois;
        this.oos = oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }
}
