package com.mycloud.usermanage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static SimpleDateFormat yyyyMMddHHmmssFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat yyyyMMddHHmmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static SimpleDateFormat yyyyMMddHHmmssSSSFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");

    public static SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm");

    public static long getValue() {
        return new Date().getTime();
    }

    public static Date getDate() {
        return new Date();
    }

    public static String formatDate(SimpleDateFormat f, Date d) {
        if (f != null && d != null) {
            return f.format(d.getTime());
        } else {
            return null;
        }
    }

    public static String formatDate(String f, Date d) {
       return formatDate(new SimpleDateFormat(f), d);
    }

    public static Date parseDate(SimpleDateFormat f, String s) {
        try {
            if (f != null && s != null) {
                return f.parse(s);
            } else {
                return null;
            }
        } catch (ParseException e) {
            logger.error("exception", e);
            return null;
        }
    }

    public static Date parseDate(String f, String s) {
        return parseDate(new SimpleDateFormat(f), s);
    }

    public static Date addMilliSeconds(Date d, int milliSeconds) {
        return addDate(d, Calendar.MILLISECOND, milliSeconds);
    }
    
    public static Date addMinutes(Date d, int minutes) {
        return addDate(d, Calendar.MINUTE, minutes);
    }

    public static Date addHours(Date d, int hours) {
        return addDate(d, Calendar.HOUR, hours);
    }
    
    public static Date addDays(Date d, int days) {
        return addDate(d, Calendar.DATE, days);
    }

    public static Date addMonths(Date d, int months) {
        return addDate(d, Calendar.MONTH, months);
    }

    public static Date addYears(Date d, int years) {
        return addDate(d, Calendar.YEAR, years);
    }

    private static Date addDate(Date d, int type, int val) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(type, val);
        return calendar.getTime();
    }
    
    public static String getToday(SimpleDateFormat f) {
        return formatDate(f, new Date());
    }
        
    //毫秒数转为天数
    public static long ms2days(long milliseconds) {
        return TimeUnit.MILLISECONDS.toDays(milliseconds);
    }
    
    //获取当前距离结束日期的剩余天数
    public static Long getBetweenDays(Date startDate, Date endDate) {
        Date a, b;
        
        try {
            a = yyyyMMddFormat.parse(yyyyMMddFormat.format(startDate));
            b = yyyyMMddFormat.parse(yyyyMMddFormat.format(endDate));
        } catch (ParseException e) {
           throw new RuntimeException(e);
        }
        
        Calendar cal = Calendar.getInstance();
        
        cal.setTime(a);
        long time1 = cal.getTimeInMillis();
        
        cal.setTime(b);
        long time2 = cal.getTimeInMillis();
        
        long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
        return betweenDays;
    }

    public static boolean before(Date d1, Date d2) {
        return d1.before(d2);
    }

    public static boolean beforeOrEqual(Date d1, Date d2) {
        return !d1.after(d2);
    }

    public static boolean after(Date d1, Date d2) {
        return d1.after(d2);
    }

    public static boolean afterOrEqual(Date d1, Date d2) {
        return !d1.before(d2);
    }

    //日期中遇到个位未补零的情况则补零(可用于任何日期格式)
    public static String toStandard(String date) {
        char[] cArr = date.toCharArray();
        int cArrLen = cArr.length;
        
        StringBuffer buf = new StringBuffer();

        if(Character.isDigit(cArr[0]) && !Character.isDigit(cArr[1])) {
            buf.append('0');
        }
        
        buf.append(cArr[0]);
        
        for(int i = 1; i < cArrLen - 1; i++) {
            if(Character.isDigit(cArr[i])) {
                if(!Character.isDigit(cArr[i - 1]) && !Character.isDigit(cArr[i + 1])) {
                    buf.append('0');
                }
            }
            buf.append(cArr[i]);
        }
        
        if(Character.isDigit(cArr[cArrLen - 1]) && !Character.isDigit(cArr[cArrLen - 2])) {
            buf.append('0');
        }
        
        buf.append(cArr[cArrLen - 1]);
        
        return buf.toString();
    }

    public static void main(String[] args) {
        System.out.println(toStandard("2015-6-1"));
        System.out.println(toStandard("2015-06-01"));
        System.out.println(toStandard("2:9:9"));
        System.out.println(toStandard("02:09:09"));
        System.out.println(toStandard("2015-6-1 2:9:9"));
        System.out.println(toStandard("2015-06-01 02:09:09"));
        System.out.println(getBetweenDays(new Date(), addYears(new Date(), 2)));
    }
}
