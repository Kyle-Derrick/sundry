package cn.adam.net.netconnect;

import cn.adam.net.netconnect.exception.NetworkIsWrongException;
import cn.adam.net.netconnect.exception.UserIsWrongException;
import cn.adam.net.netconnect.model.Status;
import cn.adam.net.netconnect.model.User;
import cn.adam.net.netconnect.util.DateUtil;
import cn.adam.net.netconnect.util.NetUtil;
import cn.adam.net.netconnect.work.Login;
import cn.adam.net.netconnect.work.ShutdownTask;
import cn.adam.net.netconnect.work.UserGet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.HttpStatusException;

import java.net.NoRouteToHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 程序运行入口
 */
@Slf4j
public class NetConnect {
    private Status status;
    private User user;

    private NetConnect(Status status, User user) {
        this.status = status;
        this.user = user;
    }

    /**
     *程序主入口
     * 先设置好定时调用gc清理垃圾，设好关闭钩子
     * 然后调用UserGet获取User用户对象，
     * 然后调用doWork，程序开始运转
     * @param args 程序的运行时的传入参数
     */
    public static void main(String[] args) {
        long period = DateUtils.MILLIS_PER_MINUTE*30L;
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.gc();
            }
        }, period, period);


        Status status = new Status();
        ShutdownTask shutdownTask = new ShutdownTask(status, Thread.currentThread());
        Runtime.getRuntime().addShutdownHook(shutdownTask);

        User user = UserGet.getUser();
        NetConnect netConnect = new NetConnect(status, user);
        boolean first = true;
        try {
            while (!netConnect.doWork(first) && status.isClose()) {
                UserGet.deleteProperties();
                netConnect.setUser(UserGet.getUser());
                first = false;
            }
        }catch (NetworkIsWrongException e){
            log.error(e.getMessage());
        }
    }

    /**
     * 主要工作区，负责网络验证，重新认证操作
     * @param first 是否第一便运转，如果是，立即进行登录操作
     * @return 返回false表示用户名密码无效，true表示正常返回
     * @throws NetworkIsWrongException 若不在华迪公寓网络，会抛出该异常
     */
    private boolean doWork(boolean first) throws NetworkIsWrongException {
        Date sd = new Date();
        while (status.isClose()){
            try {
                if (first || !NetUtil.checkInternet()){
//                    System.out.print("\r\b网络连接已断开，尝试重连...\t"+ DateUtil.getDate());
                    if (first){
                        first = false;
                    }else {
                        log.error("网络认证已失效，尝试重新认证...");
                    }
                    Login.login(user);
                    sd = new Date();
                }else {
                    String period = DateUtil.getPeriod(sd);
//                    System.out.print("\r\b网络连接正常...\t" + period);
                    log.debug("网络认证正常...\t已使用" + period);
                }
            }catch (RuntimeException e){
                throw e;
            }catch (NoRouteToHostException e){
                while (status.isClose() && !NetUtil.checkLAN()) {
                    log.debug("网线或wifi已断开连接！");
                    try {
                        Thread.sleep(500);
                    } catch (Throwable ignored) {}
                }
            }catch (HttpStatusException e){
                if (e.getStatusCode() == 403){
                    throw NetworkIsWrongException.get();
                }
            }catch (UserIsWrongException e){
                return false;
            }catch (Throwable e){
//                System.out.println("\r\b"+e.getMessage()+"\t"+ DateUtil.getDate());
                log.error(e.getMessage());
            }
            try {
                Thread.sleep(500);
            } catch (Throwable ignored) {}
        }

        return true;
    }

    private void setUser(User user) {
        this.user = user;
    }
}
