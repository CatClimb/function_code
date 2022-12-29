package com.weipu.dx45gdata.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {
    /**
     * 获取当前时间
     *
     * @param format 格式 例如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentDate(String format) {
        SimpleDateFormat s = new SimpleDateFormat(format);
        return s.format(new Date());
    }

    /**
     * 获取上一个月
     *
     * @param format 格式 例如：yyyy-MM
     */
    public static String getPreDate(String format) {
        SimpleDateFormat ss = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        return ss.format(c.getTime());
    }

    /**
     * 获取当前时间的前一小时
     * @param format
     * @return
     */
    public static String getsDate(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

        String hour = df.format(calendar.getTime());
        return hour;
    }


//    public static void main(String[] args) {
//        System.out.println(getCurrentDate("yyyyMMddHHmmss"));
//        System.out.println(getPreDate("yyyyMM"));
//        System.out.println(strToDate("20110101000000") );
//        System.out.println(getsDate("HH"));
//        System.out.println(getInteger() );
//
//    }

    /**
     * 将字符转为时间格式 2020-01-01 20:30:00.0
     *
     * @param strDate
     * @return
     */
    public static String strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = null;
        try {
            d = sdf.format(format.parse(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 获取当前时间的前一个小时
     */
    public static String getInteger() {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH0000");
        String date = df.format(calendar.getTime());
//        logger.info("一个小时前的时间：" + date);
//        logger.info("当前的时间：" + df.format(new Date()));
        return date;
    }

    /**
     * 获取当前时间的前2个小时
     */
    public static String getInteger2() {
        Calendar calendar = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH0000");
        String date = df.format(calendar.getTime());
        // logger.info("一个小时前的时间：" + date);
        //logger.info("当前的时间：" + df.format(new Date()));
        return date;
    }

    /**
     * 将字符转为时间格式 2020-01-01 20:30:00.0
     *
     * @param strDate
     * @return
     */
    public static Timestamp strToDate2(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timestamp timestamp = new Timestamp(d.getTime());
        return timestamp;
    }
}
