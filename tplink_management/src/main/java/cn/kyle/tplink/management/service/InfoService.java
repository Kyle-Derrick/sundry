package cn.kyle.tplink.management.service;

import cn.kyle.tplink.management.exception.LoginException;
import cn.kyle.tplink.management.model.WanInfo;

/**
 * 信息获取
 * @author Kyle
 */
public interface InfoService extends BaseService {
    /**
     * 获取网口信息
     * @return 返回信息
     */
    WanInfo wanStatus();
}
