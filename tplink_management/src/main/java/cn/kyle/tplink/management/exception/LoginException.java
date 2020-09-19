package cn.kyle.tplink.management.exception;

import java.util.Objects;

/**
 * 登陆异常
 * @author Kyle
 */
public class LoginException extends Exception {
    private static LoginException e;

    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }

    public static LoginException get() {
        return Objects.isNull(e) ? new LoginException() : e;
    }
}
