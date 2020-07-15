package server;

import function.User;

public class UserDB {
    public static int idCnt = 0;
    public static User user = new User("教师", "boy");
    static {
        user.setId(++idCnt);
    }
}
