package cn.kyle.tplink.management.service;

import cn.kyle.tplink.management.model.TplinkConfig;
import lombok.AllArgsConstructor;

/**
 * 基础Service
 */
public interface BaseService {
    String ip();

    String pwd();

    String stock();

    TplinkConfig config();

    String url(String path);
}
