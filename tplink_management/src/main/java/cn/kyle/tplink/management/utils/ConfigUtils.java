package cn.kyle.tplink.management.utils;

import cn.kyle.tplink.management.model.TplinkConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class ConfigUtils {
    private static final String DEFAULT_HOST = "192.168.0.1";
    private static final String READ_FILE_ERROR = "读取配置文件出错!";
    private static final String FILE_NOT_FOUND = "配置文件不存在!";
    private static final String PWD_NOT_FOUND = "未找到密码配置信息（password）！";
    private static final String HOST = "host";
    private static final String PWD = "password";
    private static final ClassLoader loader = ConfigUtils.class.getClassLoader();

    public static TplinkConfig buildConfig(){
        File file = new File("tplink.properties");
        try {
            if (!file.exists()) {
                    URL resource = loader.getResource("tplink.properties");
                    Objects.requireNonNull(resource);
                    file = new File(resource.getFile());
                    if (!file.exists()) {
                        throw new RuntimeException();
                    }
            }

            return buildConfig(new FileReader(file));
        }catch (Exception e) {
            throw new RuntimeException(FILE_NOT_FOUND);
        }
    }

    public static TplinkConfig buildConfig(String path){
        try {
            return buildConfig(new FileReader(path));
        } catch (FileNotFoundException e) {
            log.error(FILE_NOT_FOUND);
            throw new RuntimeException(e);
        }
    }

    public static TplinkConfig buildConfig(Reader reader){
        Properties properties = new Properties();
        try {
            properties.load(reader);
            String host = properties.getProperty(HOST, DEFAULT_HOST);
            String password = properties.getProperty(PWD);
            if (Objects.isNull(password)) {
                throw new RuntimeException(PWD_NOT_FOUND);
            }

            return new TplinkConfig().setIp(host).setPwd(password);
        } catch (IOException e) {
            log.error(READ_FILE_ERROR);
            throw new RuntimeException(e);
        }finally {
            try {
                if (null != reader) { reader.close(); }
            }catch (Exception ignored){}
        }
    }
}
