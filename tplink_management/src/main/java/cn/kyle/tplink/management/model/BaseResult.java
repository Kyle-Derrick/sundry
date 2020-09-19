package cn.kyle.tplink.management.model;

import cn.kyle.tplink.management.enumerate.ResultErrorCode;
import cn.kyle.tplink.management.model.Network;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResult {
    private Network network;
    @JSONField(name = "error_code")
    private int errorCode;
}
