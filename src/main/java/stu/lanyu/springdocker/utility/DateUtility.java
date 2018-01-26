package stu.lanyu.springdocker.utility;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

public class DateUtility {

    /**
     * 输出当前为指定的时间格式
     * @param dateFormatString 时间格式, 默认值: yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getDateNowFormat(String dateFormatString) {

        if (StringUtility.isNullOrEmpty(dateFormatString))
            dateFormatString = "yyyy-MM-dd HH:mm:ss";

        return new SimpleDateFormat(dateFormatString).format(new Date()) ;
    }

    public static Date getDate(long dateTicks) {
        return Date.from(Instant.ofEpochMilli(dateTicks).atZone(ZoneId.of("UTC")).toInstant());
    }

    /**
     * 相距时间差以小时为单位
     * @param date 与现在进行比较的时间
     * @return
     */
    public static double compareFormNowByHour(Date date) {

        long diff = Math.abs(new Date().getTime() - date.getTime());
        double diffDays = ((double)diff) / ((double)(60 * 60 * 1000));
        return diffDays;
    }

    /**
     * 相距时间差以分为单位
     * @param date 与现在进行比较的时间
     * @return
     */
    public static double compareFormNowByMinute(Date date) {

        long diff = Math.abs(new Date().getTime() - date.getTime());
        double diffDays = ((double)diff) / ((double)(60 * 1000));
        return diffDays;
    }

    /**
     * 相距时间差以秒为单位
     * @param date 与现在进行比较的时间
     * @return
     */
    public static double compareFormNowBySecond(Date date) {

        long diff = Math.abs(new Date().getTime() - date.getTime());
        double diffDays = ((double)diff) / ((double)1000);
        return diffDays;
    }

    /**
     * 相距时间差以天为单位
     * @param date 与现在进行比较的时间
     * @return
     */
    public static double compareFormNowByDay(Date date) {

        long diff = Math.abs(new Date().getTime() - date.getTime());
        double diffDays = ((double)diff) / ((double)(24 * 60 * 60 * 1000));
        return diffDays;
    }
}
