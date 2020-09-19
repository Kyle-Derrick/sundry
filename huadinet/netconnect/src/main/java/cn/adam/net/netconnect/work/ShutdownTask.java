package cn.adam.net.netconnect.work;

import cn.adam.net.netconnect.model.Status;
import lombok.extern.slf4j.Slf4j;

/**
 * 在程序关闭时将状态改为关闭状态，然后等待程序自然结束
 */
@Slf4j
public class ShutdownTask extends Thread {
    private Status status;
    private Thread mt;

    public ShutdownTask(Status status, Thread mt) {
        this.status = status;
        this.mt = mt;
    }

    @Override
    public void run() {
        status.close();
        log.info("等待程序终止...");
        try {
            mt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("\r\b程序关闭!");
        log.info("程序关闭!");
    }
}
