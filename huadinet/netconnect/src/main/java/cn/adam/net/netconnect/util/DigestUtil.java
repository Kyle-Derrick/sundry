package cn.adam.net.netconnect.util;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 程序需要用到的加密处理，使用MD5
 */
public class DigestUtil {
    private static MessageDigest md5;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 传入加密参数，获取加密后的登录密码
     * @param pw 输入的登录密码
     * @param ip IP地址
     * @param mac MAC地址
     * @return 返回加密后的登录密码
     */
    public static String getSumPassWord(String pw, String ip, String mac){
        pw = Hex.encodeHexString(md5.digest(pw.getBytes()));
        return Hex.encodeHexString(md5.digest((pw+ip+mac).getBytes()));
    }
}
