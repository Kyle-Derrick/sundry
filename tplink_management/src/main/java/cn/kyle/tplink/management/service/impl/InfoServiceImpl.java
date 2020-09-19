package cn.kyle.tplink.management.service.impl;

import cn.kyle.tplink.management.enumerate.TplinkMethod;
import cn.kyle.tplink.management.model.*;
import cn.kyle.tplink.management.service.InfoService;
import cn.kyle.tplink.management.utils.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InfoServiceImpl extends BaseServiceImpl implements InfoService {
    private static final String WAN_INFO_URL = "/stok=%s/ds";

    public InfoServiceImpl(TplinkConfig config) {
        super(config);
    }

    @Override
    public WanInfo wanStatus() {
        JSONObject json = HttpUtils.post(
                wanInfoUrl(),
                new BaseParam()
                        .setMethod(TplinkMethod.GET)
                        .setNetwork(new Network().setName(Network.WAN_STATUS_NAME))
        );

        BaseResult result = json.toJavaObject(BaseResult.class);
        if (Objects.isNull(result) || Objects.isNull(result.getNetwork())) {
            return null;
        }
        return result.getNetwork().getWanStatus();
    }

    private String wanInfoUrl(){
        return url(String.format(WAN_INFO_URL, stock()));
    }
}
