package cn.adam.net.netconnect.exception;

/**
 * 用户名密码错误异常
 */
public class UserIsWrongException extends Exception {
    private static UserIsWrongException userIsWrongException =
            new UserIsWrongException("账号密码错误");

    private UserIsWrongException(String message) {
        super(message);
    }

    public static UserIsWrongException get() {
        return userIsWrongException;
    }
}
