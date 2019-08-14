package cn.ac.caict.iiiiot.idisc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	static final String DATE_FORMAT_STD = "yyyy-MM-dd HH:mm:ss";
	public static Date parseString2Date(String strDate) throws Exception {
        if (strDate == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_STD);
        return format.parse(strDate);
    }
	
	public static String parseDate2String(Date date) throws Exception {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_STD);
        return format.format(date);
    }

}
