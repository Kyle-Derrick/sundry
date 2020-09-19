package cn.adam.net.netconnect.model;

/**
 * 用于表示当前是否为关闭状态，若为关闭状态程序将进入结束阶段
 */
public class Status {
    private volatile boolean close = false;

    public synchronized void close(){
        this.close = true;
    }
    public synchronized boolean isClose(){
        return !this.close;
    }
}
