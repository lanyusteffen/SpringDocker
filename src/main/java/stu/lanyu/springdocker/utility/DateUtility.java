package stu.lanyu.springdocker.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility {
    public static String getDateNowFormat(String dateFormatString) {
        if (StringUtility.isNullOrEmpty(dateFormatString))
            dateFormatString = "yyyy/MM/dd HH:mm:ss";

        return new SimpleDateFormat(dateFormatString).format(new Date()) ;
    }
}
