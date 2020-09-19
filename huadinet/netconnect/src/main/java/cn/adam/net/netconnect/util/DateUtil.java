package cn.adam.net.netconnect.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 处理时间的工具类，包括
 *
 */
public class DateUtil {
//    private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public static String getDate() {
//        return dateformat.format(new Date());
//    }

    /**
     * 获取某事件与当前时间的时间差，格式为**小时**秒，若（**小时|**分）为0则去掉（小时|分）单位
     * @param date 传入历史时间
     * @return 返回时间差字符串，如*小时**分**秒，*分**秒，*秒
     */
    public static String getPeriod(Date date) {
        if (Objects.isNull(date))
            return "0秒";
        return DurationFormatUtils.formatPeriod(date.getTime(), new Date().getTime(), "H小时m分s秒")
                .replaceAll("(0小时|0分)+", "");
    }
}
