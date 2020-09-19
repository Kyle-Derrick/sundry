package cn.adam.net.netconnect.work;

import cn.adam.net.netconnect.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * 获取用户信息（来源于控制台或者用户信息文件）
 */
@Slf4j
public class UserGet {
    private static final File config = new File("config.properties");

    /**
     * 调用该方法先尝试从用户信息文件获取用户信息，
     * 若没有该文件，则从控制台获取，等待用户输入
     * @return 返回User用户对象
     */
    public static User getUser() {
        User user = getUserByProperties();
        if (Objects.isNull(user)){
            user = getUserByConsole();
            writeUserToProperties(user);
        }

        return user;
    }

    /**
     * 尝试从用户信息文件获取用户对象
     * @return 返回User对象，若获取失败返回null
     */
    private static User getUserByProperties(){
        if (!config.exists())
            return null;

        User user;
        log.info("获取已保存用户...");
        try (FileReader reader = new FileReader(config)){
            Properties properties = new Properties();
            properties.load(reader);

            user = new User(properties.getProperty("username"), properties.getProperty("password"));
            if (Objects.isNull(user.getUsername()) || Objects.isNull(user.getPassword()))
                user = null;
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return user;
    }

    /**
     * 保存用户信息
     * @param user 传入用户对象
     */
    private static void writeUserToProperties(User user){
        try (FileWriter writer = new FileWriter(config)){
            Properties properties = new Properties();
            properties.setProperty("username", user.getUsername());
            properties.setProperty("password", user.getPassword());
            log.info("保存账号密码...");
            properties.store(writer, "auto save");
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 从控制台获取用户信息
     * @return 返回用户对象
     */
    private static User getUserByConsole(){
        User user;
//        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        String username;
        String password;
        while (true){
            System.out.print("请输入登录用户名(登录成功后会被保存): ");
//            username = scanner.nextLine();
            username = console.readLine();
            System.out.print("密码: ");
//            password = scanner.nextLine();
            password = new String(console.readPassword());
            if (!StringUtils.isAnyEmpty(username, password)) {
                user = new User(username, password);
                break;
            }else {
//                System.out.println("错误：用户名或者密码为空！！！");
                log.info("错误：用户名或者密码为空！！！请重新输入。");
            }
        }

        return user;
    }

    /**
     * 删除用户信息文件
     * 登录失败时会调用
     */
    public static void deleteProperties(){
        assert config.delete();
    }
}
