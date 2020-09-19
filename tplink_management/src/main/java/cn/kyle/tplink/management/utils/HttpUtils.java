package cn.kyle.tplink.management.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Http请求工具类
 * @author Kyle
 */
@Slf4j
public class HttpUtils {

    /**
     * payload get请求
     * @param url 请求地址
     * @return 返回JSON
     */
    public static JSONObject get(String url){
        return http(url, null, Method.GET);
    }

    /**
     * payload get请求
     * @param url 请求地址
     * @param data 请求数据
     * @return 返回JSON
     */
    public static JSONObject post(String url, String data){
        return http(url, data, Method.POST);
    }

    public static JSONObject post(String url, Object data){
        return post(url, JSON.toJSONString(data));
    }

    /**
     * payload get请求(form)
     * @param url 请求地址
     * @param data 请求数据
     * @return 返回JSON
     */
    public static JSONObject postForm(String url, Object data){
        try {
            return postForm(url, BeanUtils.describe(data));
        } catch (Exception e) {
            log.error("请求出错!", e);
        }
        return null;
    }

    public static JSONObject postForm(String url, Map<String, String> data){
        return httpForm(url, data, Method.POST);
    }

    /**
     * payload请求
     * @param url 地址
     * @param data 数据
     * @param method 请求方法
     * @return 返回JSON对象
     */
    public static JSONObject http(String url, String data, Method method){
        Connection connection = createConnection(url, method)
                .header("Content-Type", "application/json; charset=UTF-8")
                .requestBody(data);
        return result(connection);
    }

    /**
     * form请求
     * @param url 地址
     * @param data 数据
     * @param method 请求方法
     * @return 返回JSON对象
     */
    public static JSONObject httpForm(String url, Map<String, String> data, Method method){
        Connection connection = createConnection(url, method)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data(data);
        return result(connection);
    }

    private static Connection createConnection(String url, Method method) {
        return Jsoup.connect(url)
//                .ignoreContentType(true)
                .header("Accept-Encoding", "gzip, deflate")
                .ignoreContentType(true)
//                .proxy("127.0.0.1", 8888)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36")
                .method(method)
                .timeout(60000);
    }

    private static JSONObject result(Connection connection) {
        Response execute;
        try {
            if (connection.request().data().size() < 1 && Objects.isNull(connection.request().requestBody())) {
                connection.ignoreContentType(true);
            }
            execute = connection.execute();
            return JSON.parseObject(execute.body());
        } catch (IOException e) {
            log.error("请求时出错", e);
        } catch (RuntimeException e) {
            log.error("请求结果转json时出错", e);
        }
        return null;
    }
}
