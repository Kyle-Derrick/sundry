package cn.adam.net.netconnect.work;

import cn.adam.net.netconnect.exception.UserIsWrongException;
import cn.adam.net.netconnect.model.Addr;
import cn.adam.net.netconnect.model.User;
import cn.adam.net.netconnect.util.DigestUtil;
import cn.adam.net.netconnect.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 处理登录逻辑
 */
@Slf4j
public class Login {
    /**
     * 用传入的用户对象进行登录
     * @param user 传入用户对象，里面包含用户名和密码
     * @throws IOException 访问网络时可能会抛出IOException异常
     * @throws UserIsWrongException 如果用户名密码不正确会抛出该异常
     */
    public static void login(User user) throws IOException, UserIsWrongException {
        log.debug("正在获取IP地址和MAC地址...");
        Addr ipMac = getIpMac();
        String sumPassWord = DigestUtil.getSumPassWord(user.getPassword(), ipMac.getIp(), ipMac.getMac());
        String url = "http://1.0.0.0/Action/webauth-up?type=1&action=release&username="
                + user.getUsername()
                + "&password="
                + sumPassWord
                + "&refer=&mac="
                +ipMac.getMac();
        log.debug("正在进行网络认证...");
        Connection.Response execute = NetUtil.connect(url).execute();
        Map<String, String> query = NetUtil.getUrlQuerys(execute.url());
        String res = query.get("res");
//        String date = DateUtil.getDate();
        if (StringUtils.equals(res, "fail")){
            String errmsg = URLDecoder.decode(query.get("errmsg"), "UTF-8");
//            System.out.println("\r\b" + date + "\t认证失败！\t"+ errmsg);
            log.error("认证失败！\t"+ errmsg);
            if (StringUtils.equals(errmsg, "账号密码错误"))
                throw UserIsWrongException.get();

        }else {
//            System.out.println("\r\b" + date + "\t认证成功！");
            log.info("认证成功！\tusername:"+user.getUsername());
        }
    }

    /**
     * 获取本机在认真服务器上的IP和MAC地址
     * @return 返回包含IP和MAC地址的对象
     * @throws IOException 访问网络可能会抛出该异常
     */
    public static Addr getIpMac() throws IOException {
        Connection.Response execute = NetUtil.connect("http://1.0.0.0")
                .execute();
        URL url = execute.url();
        Map<String, String> query = NetUtil.getUrlQuerys(url);

        return new Addr(query.get("user_ip"), query.get("mac"));
    }
}
