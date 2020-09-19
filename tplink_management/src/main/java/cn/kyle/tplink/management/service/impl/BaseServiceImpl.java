package cn.kyle.tplink.management.service.impl;

import cn.kyle.tplink.management.service.BaseService;
import lombok.AllArgsConstructor;
import cn.kyle.tplink.management.model.TplinkConfig;

/**
 * 基础Service
 */
@AllArgsConstructor
public abstract class BaseServiceImpl implements BaseService {
    /**
     * 配置信息
     */
    private TplinkConfig config;

    @Override
    public String ip() {
        return this.config.getIp();
    }

    @Override
    public String pwd() {
        return this.config.getPwd();
    }

    @Override
    public String stock() {
        return this.config.getStock();
    }

    @Override
    public TplinkConfig config() {
        return config;
    }

    @Override
    public String url(String path) {
        return "http://".concat(this.config.getIp()).concat(path);
    }
}
