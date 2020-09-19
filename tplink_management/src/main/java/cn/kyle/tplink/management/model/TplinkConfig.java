package cn.kyle.tplink.management.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Tplink配置信息
 * @author Kyle
 */
@Data
@Accessors(chain = true)
public class TplinkConfig {
    private String ip;
    private String pwd;
    private String stock;
}
