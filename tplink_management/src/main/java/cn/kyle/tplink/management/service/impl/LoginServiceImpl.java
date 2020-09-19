package cn.kyle.tplink.management.service.impl;

import cn.kyle.tplink.management.exception.LoginException;
import com.alibaba.fastjson.JSONObject;
import cn.kyle.tplink.management.enumerate.TplinkMethod;
import cn.kyle.tplink.management.model.BaseParam;
import cn.kyle.tplink.management.model.LoginParam;
import cn.kyle.tplink.management.model.TplinkConfig;
import cn.kyle.tplink.management.service.LoginService;
import cn.kyle.tplink.management.utils.HttpUtils;
import cn.kyle.tplink.management.utils.TplinkUtils;

import java.util.Objects;

public class LoginServiceImpl extends BaseServiceImpl implements LoginService {
    private static final String LOGIN_URL = "/";

    public LoginServiceImpl(TplinkConfig config) {
        super(config);
    }

    @Override
    public String login(String pwd) throws LoginException {
        String authPwd = TplinkUtils.orgAuthPwd(pwd);

        JSONObject json = HttpUtils.post(url(LOGIN_URL), new BaseParam().setMethod(TplinkMethod.DO).setLogin(new LoginParam(authPwd)));
        String stok = json.getString("stok");
        if (Objects.isNull(stok)) {
            throw LoginException.get();
        }

        return saveStock(stok);
    }

    private String saveStock(String stock) {
        config().setStock(stock);
        return stock;
    }
}
