package cn.kyle.tplink.management.service;

import cn.kyle.tplink.management.exception.LoginException;

/**
 * 登录
 * @author Kyle
 */
public interface LoginService extends BaseService {
    /**
     * 登录，获取stock
     * @param pwd 密码
     * @return 返回stock
     */
    String login(String pwd) throws LoginException;
}
