package cn.adam.net.netconnect.util;

import cn.adam.net.netconnect.exception.NetworkIsWrongException;
import cn.adam.net.netconnect.work.Login;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络相关工具类
 */
@Slf4j
public class NetUtil {
    /**
     * 检查当前网络是否可用
     * @return 返回true表示可用，false表示不可用
     */
    public static boolean checkInternet() {
//        return ping("www.baidu.com", 3);
        return ping("cdn.bootcss.com");
    }

    /**
     * 检查所处网络不是华迪公寓网络，网络连接是否断开
     * @return true表示是华迪公寓网络，false表示网络连接已断开
     * @throws NetworkIsWrongException 抛出异常，当前网络并非华迪公寓网络
     */
    public static boolean checkLAN() throws NetworkIsWrongException {
        boolean ping = ping("1.0.0.0");
        if (ping){
            try {
                Login.getIpMac();
            } catch (Throwable e) {
                throw NetworkIsWrongException.get();
            }
        }
        return ping;
    }

    /**
     * 使用ping的方法检测网络是否通畅
     * @param host 目标主机
     * @return 返回true表示网络连接正常，false表示无网络连接
     */
    private static boolean ping(String host) {
        boolean result = false;
        try {
            Process exec = Runtime.getRuntime().exec("ping -n 3 "+host);
            StringBuilder sb = new StringBuilder();
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream(), "GBK"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    sb.append(line);
            }
            if (sb.toString().indexOf("TTL")>0)
                result = true;
        } catch (Exception e) {
            log.error("ping "+host+"时出错！");
        }

        return result;
    }

    /**
     * 根据传入的url地址获取url中的参数
     * @param url 传入url地址
     * @return 返回传入url里的参数的Map集合
     */
    public static Map<String, String> getUrlQuerys(URL url) {
        String[] qs = url.getQuery().split("&");
        Map<String, String> result = new HashMap<>();
        for (String query : qs) {
            String[] split = query.split("=");
            result.put(
                    split[0],
                    split.length<2?split[0]:split[1]
            );
        }

        return result;
    }

    private static final Map<String, String> headers;
    static {
        headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
    }

    /**
     * 获取带有常规浏览器的header的Connection（来自Jsoup包），达到模拟浏览器的效果
     * @param url 传入url地址
     * @return 返回Jsoup的Connection对象
     */
    public static Connection connect(String url){
        return Jsoup.connect(url).headers(headers);
    }
}
