package cn.kyle.tplink.management.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class WanInfo {
    @JSONField(name = "down_speed")
    private int downSpeed;
    @JSONField(name = "up_speed")
    private int upSpeed;
    private String ipaddr;
    @JSONField(name = "link_status")
    private int linkStatus;
    @JSONField(name = "phy_status")
    private int phyStatus;
    @JSONField(name = "error_code")
    private int errorCode;
    private String proto;
    @JSONField(name = "pri_dns")
    private String priDns;
    private String gateway;
    @JSONField(name = "up_time")
    private int upTime;
    @JSONField(name = "snd_dns")
    private String sndDns;
    private String netmask;
}
