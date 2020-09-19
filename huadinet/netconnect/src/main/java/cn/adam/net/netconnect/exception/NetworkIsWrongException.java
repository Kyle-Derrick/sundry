package cn.adam.net.netconnect.exception;

/**
 * 所处网络并非华迪公寓网络异常
 */
public class NetworkIsWrongException extends Exception {
    private static NetworkIsWrongException networkIsWrongException =
            new NetworkIsWrongException("所处网络并非华迪公寓网络！");

    private NetworkIsWrongException(String message) {
        super(message);
    }

    public static NetworkIsWrongException get(){
        return networkIsWrongException;
    }
}
