package cn.ac.caict.iiiiot.id.client.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;

public class DateUtils {
	static final String DATE_FORMAT_STD = "yyyy-MM-dd HH:mm:ss";
	public static Date parseString2Date(String strDate) throws IdentifierException {
        if (strDate == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_STD);
        try {
			return format.parse(strDate);
		} catch (ParseException e) {
			throw new IdentifierException(ExceptionCommon.TIME_PARSE_ERROR);
		}
    }
	
	public static String parseDate2String(Date date) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_STD);
        return format.format(date);
    }
	
	public static long parseString2Secs(String strDate) throws IdentifierException {
        if (strDate == null) {
            return 0;
        }
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_STD);
        try {
			return format.parse(strDate).getTime()/1000L;
		} catch (ParseException e) {
			throw new IdentifierException(ExceptionCommon.TIME_PARSE_ERROR);
		}
    }

}
