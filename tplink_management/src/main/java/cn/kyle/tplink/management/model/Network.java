package cn.kyle.tplink.management.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Network {
    public static final String WAN_STATUS_NAME = "wan_status";

    @JSONField(name = "wan_status")
    private WanInfo wanStatus;
    private String name;
}
