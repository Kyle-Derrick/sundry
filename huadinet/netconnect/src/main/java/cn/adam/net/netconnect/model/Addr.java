package cn.adam.net.netconnect.model;

/**
 * 用于储存IP值和MAC值
 */
public class Addr {
    private String ip;
    private String mac;

    public Addr(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }
}
