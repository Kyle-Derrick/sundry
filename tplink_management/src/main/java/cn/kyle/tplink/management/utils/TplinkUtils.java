package cn.kyle.tplink.management.utils;

/**
 * Tplink 相关工具类
 * @author Kyle
 */
public class TplinkUtils {
    private static final String DEFAULT_STR_DE = "RDpbLfCPsJZ7fiv";
    private static final String DEFAULT_DIC = "yLwVl0zKqws7LgKPRQ84Mdt708T1qQ3Ha7xv3H7NyU84p21BriUWBU43odz3iP4rBL3cD02KZciX"+
            "TysVXiV8ngg6vL48rPJyAUw0HurW20xqxv9aYb4M9wK1Ae0wlro510qXeU07kV57fQMc8L6aLgML"+
            "wygtc0F10a0Dg70TOoouyFhdysuRMO51yY5ZlOZZLEal1h0t9YQW0Ko7oBwmCAHoic4HYbUyVeU3"+
            "sfQ1xtXcPcf1aT303wAQhv66qzW";

    /**
     * 密码加密
     * @param pwd 密码
     * @return 返回加密字符串
     */
    public static String orgAuthPwd(String pwd) {
        return securityEncode(pwd, DEFAULT_STR_DE, DEFAULT_DIC);
    }

    /**
     * 加密算法
     * @param str 字符串
     * @param strDe *
     * @param dic 字典
     * @return 返回加密字符串
     */
    @SuppressWarnings("WeakerAccess")
    public static String securityEncode(String str, String strDe, String dic) {
        StringBuilder output = new StringBuilder();
        int len, len1, len2, lenDict;
        char cl, cr;

        len1 = str.length();
        len2 = strDe.length();
        lenDict = dic.length();
        len = Math.max(len1, len2);

        for (int index = 0; index < len; index++)
        {
            cl = 0xBB;
            cr = 0xBB;

            if (index >= len1)
            {
                cr = strDe.charAt(index);
            }
            else if (index >= len2)
            {
                cl = str.charAt(index);
            }
            else
            {
                cl = str.charAt(index);
                cr = strDe.charAt(index);
            }

            output.append(dic.charAt((cl ^ cr) % lenDict));
        }
        return output.toString();
    }
}
