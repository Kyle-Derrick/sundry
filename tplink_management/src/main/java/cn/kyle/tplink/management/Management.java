package cn.kyle.tplink.management;

import cn.kyle.tplink.management.exception.LoginException;
import cn.kyle.tplink.management.model.TplinkConfig;
import cn.kyle.tplink.management.model.WanInfo;
import cn.kyle.tplink.management.service.BaseService;
import cn.kyle.tplink.management.service.InfoService;
import cn.kyle.tplink.management.service.LoginService;
import cn.kyle.tplink.management.service.impl.InfoServiceImpl;
import cn.kyle.tplink.management.service.impl.LoginServiceImpl;
import cn.kyle.tplink.management.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Management {
    private static final String IMPL_SUFFIX = "Impl";
    private static final String IMPL_PATH = ".impl.";
    private TplinkConfig config;
    private Map<String, BaseService> services;

    public Management(String ip, String pwd) {
        this(new TplinkConfig().setIp(ip).setPwd(pwd));
    }
    public Management(TplinkConfig config) {
        this.config = config;
        this.services = new HashMap<>();
    }
    public Management() {
        this(ConfigUtils.buildConfig());
    }

    public static void main(String[] args) throws LoginException {
//        Management management = new Management("192.168.1.1", "qwe123,./");
        Management management = new Management();
        management.login();
        WanInfo wanIfo = management.getWanIfo();

        System.out.println(management.config.getStock());
        System.out.println(wanIfo);
    }

    public void login() throws LoginException {
        LoginService loginService = (LoginService) getService("login_service", LoginServiceImpl.class);
        config.setStock(loginService.login(this.config.getPwd()));
    }

    public WanInfo getWanIfo() {
        InfoService infoService = (InfoService) getService("info_service", InfoServiceImpl.class);
        return infoService.wanStatus();
    }

    private BaseService getService(String name, Class<? extends BaseService> clazz) {
        Objects.requireNonNull(clazz);
        BaseService baseService = services.get(name);
        if (Objects.isNull(baseService)) {
            try {
                baseService = clazz.getConstructor(TplinkConfig.class).newInstance(this.config);
            } catch (Exception e) {
                log.info(clazz.getSimpleName());
                log.info(clazz.getName());
                if (!clazz.getSimpleName().endsWith(IMPL_SUFFIX)) {
                    return reGetService(clazz);
                }
                log.error("创建Service实例出错！", e);
                throw new RuntimeException(e);
            }
            services.put(name, baseService);
        }
        return baseService;
    }

    private BaseService reGetService(Class<? extends BaseService> clazz) {
        try {
            Class<?> c = Class.forName(clazz.getPackage().getName().concat(IMPL_PATH).concat(clazz.getSimpleName()).concat(IMPL_SUFFIX));
            return (BaseService) c.getConstructor(TplinkConfig.class).newInstance(this.config);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
