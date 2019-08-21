package com.square.common.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期公用类
 */
public class DateUtils {

    public static final String DATE_SHORT_FORMAT = "yyyy-MM-dd";

    public static final String DATE_FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIGHT__FORMAT = "yyyyMMddHHmm";

    public static final String DEFAULT_DATE_FORMAT = DATE_SHORT_FORMAT;

    private static final String DATE_UTE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";

    /**
     * 字符串按默认格式转日期
     *
     * @param strDate 日期字符串
     * @return 日期
     * @throws ParseException
     */
    public static Date parse(String strDate) throws ParseException {
        return parse(strDate, DEFAULT_DATE_FORMAT);
    }

    /**
     * 字符串按默认格式转日期
     *
     * @param strDate 日期字符串
     * @return 日期
     * @throws ParseException
     */
    public static Date parseTime(String strDate) throws ParseException {
        return parse(strDate, DATE_FULL_FORMAT);
    }

    /**
     * 字符串按指定格式转日期
     *
     * @param strDate 日期字符串
     * @param pattern 指定的日期转换格式
     * @return 日期
     * @throws ParseException
     */
    public static Date parse(String strDate, String pattern) {
        Date date = null;
        try {
            date = createDateFormat(pattern).parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getMinOfDay(String dataTime) throws ParseException {
        Date dt = DateUtils.parse(dataTime, DateUtils.DATE_FULL_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int minOfDay = h * 60 + m;
        return minOfDay;
    }

    /**
     * 日期按默认格式转字符串
     *
     * @param date 这个日期值将格式化为一个日期字符串
     * @return 日期字符串
     */
    public static String format(Date date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * UTC 字符串格式转换成当地日期
     *
     * @param strDate 日期字符串 如 2007-10-23T17:15:44.000Z
     * @return 日期
     * @throws ParseException
     */
    public static Date dateFromStrUTC(String strDate) throws ParseException {
        if (StringUtils.isBlank(strDate)) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            strDate = strDate.replace("Z", " UTC");
            return createDateFormat(DATE_UTE_FORMAT).parse(strDate);
        }
    }

    /**
     * 日期按指定格式转字符串
     *
     * @param date    这个日期值将格式化为一个日期字符串
     * @param pattern 指定的日期转换格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            return createDateFormat(pattern).format(date);
        }
    }

    /**
     * 创建日期格式化实现类
     *
     * @param pattern 指定的日期转换格式
     * @return 日期格式化实现类
     */
    private static DateFormat createDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 指定日期增加（年）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值增量
     * @return 新日期
     */
    public static Date addYear(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static String addYear(String date, int amount) {
        return format(addYear(parse(date, DATE_FULL_FORMAT), amount), DATE_FULL_FORMAT);
    }

    /**
     * 指定日期增加（月）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值增量
     * @return 新日期
     */
    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static String addMonth(String date, int amount) {
        return format(addMonth(parse(date, DATE_FULL_FORMAT), amount), DATE_FULL_FORMAT);
    }

    /**
     * 指定日期增加（周）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值增量
     * @return 新日期
     */
    public static Date addWeek(Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static String addWeek(String date, int amount) {
        return format(addWeek(parse(date, DATE_FULL_FORMAT), amount), DATE_FULL_FORMAT);
    }

    /**
     * 指定日期增加（天）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值增量
     * @return 新日期
     */
    public static Date addDay(Date date, int amount) {
        return add(date, Calendar.DATE, amount);
    }

    public static String addDay(String time, Integer day) {
        return format(addDay(parse(time, DATE_FULL_FORMAT), day), DATE_FULL_FORMAT);
    }


    /**
     * 指定日期增加（天）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值增量
     * @return 新日期
     */
    public static Date addHour(Date date, int amount) {
        return add(date, Calendar.HOUR, amount);
    }

    public static String addHour(String time, Integer day) {
        return format(addHour(parse(time, DATE_FULL_FORMAT), day), DATE_FULL_FORMAT);
    }

    /**
     * 指定日期减少（天）
     *
     * @param date   指定的一个原始日期
     * @param amount 数值减量
     * @return 新日期
     */
    public static Date reduceDay(Date date, int amount) {
        return add(date, Calendar.DATE, -amount);
    }

    /**
     * 指定日期增加数量（年，月，日，小时，分钟，秒，毫秒）
     *
     * @param date   指定的一个原始日期
     * @param field  日历类Calendar字段
     * @param amount 数值增量
     * @return
     */
    public static Date add(Date date, int field, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = createCalendar(date);
            c.add(field, amount);
            return c.getTime();
        }
    }

    /**
     * 获取日期是一年中的第几周
     *
     * @param date 指定的一个原始日期
     * @return
     */
    public static int getWeekOfYear(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar calendar = createCalendar(date);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            return calendar.get(Calendar.WEEK_OF_YEAR);
        }
    }


    /**
     * 获取日期是星期几(0~6,0为星期日)
     *
     * @param date 指定的一个原始日期
     * @return
     */
    public static int getWeekOfDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            return createCalendar(date).get(Calendar.DAY_OF_WEEK) - 1;
        }
    }

    /**
     * 根据当前日期及生日,计算年龄,返回”*岁*个月*天”
     *
     * @param birthday 生日日期
     * @param desDay   目标日期
     * @return
     */
    public static Age getAge(Date birthday, Date desDay) {
        if (birthday == null || desDay == null) {
            throw new IllegalArgumentException("The birthday and desDay must not be null");
        }
        Calendar bCalendar = createCalendar(birthday);
        Calendar dCalender = createCalendar(desDay);
        int day = dCalender.get(Calendar.DAY_OF_MONTH) - bCalendar.get(Calendar.DAY_OF_MONTH);
        int month = dCalender.get(Calendar.MONTH) - bCalendar.get(Calendar.MONTH);
        int year = dCalender.get(Calendar.YEAR) - bCalendar.get(Calendar.YEAR);
        // 按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
        if (day < 0) {
            month--;
            // 得到上一个月，用来得到上个月的天数。
            dCalender.add(Calendar.MONTH, -1);
            day = day + dCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (month < 0) {
            year--;
            month = (month + 12) % 12;
        }
        return new Age(year, month, day);
    }

    /**
     * 创建日历类
     *
     * @param date 指定日期
     * @return
     */
    private static Calendar createCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * 年龄类
     */
    public static final class Age {
        private int year;
        private int month;
        private int day;

        public Age(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(year).append("岁").append(month).append("个月").append(day).append("天")
                    .toString();
        }
    }

    /**
     * Description: 返回两个时间相隔的天数
     *
     * @param preDatetime
     * @param afterDatetime
     * @param hasFloat
     * @return
     * @author 潘深练
     * @Version 1.0 2014-4-18下午01:47:36
     */
    public static float getDaysByCompare(String preDatetime, String afterDatetime, Boolean hasFloat) {
        return getByCompare(preDatetime, afterDatetime, 1, hasFloat);
    }

    public static float getDaysByCompare(String preDatetime, String afterDatetime) {
        return getByCompare(preDatetime, afterDatetime, 1, true);
    }

    /**
     * Description: 返回两个日期相隔的月数
     *
     * @param preDate
     * @param afterDate
     * @return
     * @author 潘深练
     */
    public static double getMonthsByCompare(String preDate, String afterDate) {
        String compareDate = preDate;
        double passMonths = 0.0d;
        while (compareDate(compareDate, afterDate) > 0) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date compareD = df.parse(compareDate);
                compareD = DateUtils.addMonth(compareD, 1);
                compareDate = df.format(compareD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            passMonths++;
        }
        return passMonths;
    }

    /**
     * Description: 返回两个日期相隔的月数
     *
     * @param preDate
     * @param afterDate
     * @return
     * @author 潘深练
     */
    public static double getMonthsByCompare(Date preDate, Date afterDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return getMonthsByCompare(df.format(preDate), df.format(afterDate));
    }

    public static int compareDate(String pre, String after) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date preD = df.parse(pre);
            Date afterD = df.parse(after);
            if (preD.getTime() < afterD.getTime()) {
                return 1;
            } else if (preD.getTime() > afterD.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * Description: 返回两个时间相隔的小时数
     *
     * @param preDatetime
     * @param afterDatetime
     * @param hasFloat
     * @return
     * @author 潘深练
     * @Version 1.0 2014-4-18下午01:47:47
     */
    public static float getHoursByCompare(String preDatetime, String afterDatetime, Boolean hasFloat) {
        return getByCompare(preDatetime, afterDatetime, 2, hasFloat);
    }

    public static float getHoursByCompare(String preDatetime, String afterDatetime) {
        return getByCompare(preDatetime, afterDatetime, 2, true);
    }

    /**
     * Description: 返回两个时间相隔的分钟数
     *
     * @param preDatetime
     * @param afterDatetime
     * @param hasFloat
     * @return
     * @author 潘深练
     * @Version 1.0 2014-4-18下午01:47:56
     */
    public static float getMinutesByCompare(String preDatetime, String afterDatetime, Boolean hasFloat) {
        return getByCompare(preDatetime, afterDatetime, 3, hasFloat);
    }

    public static float getMinutesByCompare(String preDatetime, String afterDatetime) {
        return getByCompare(preDatetime, afterDatetime, 3, true);
    }

    public static int getMinutesByIntCompare(Date preDatetime, Date afterDatetime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Float f = new Float(getByCompare(sf.format(preDatetime), sf.format(afterDatetime), 3, false));
        return f.intValue();
    }

    /**
     * Description: 返回两个时间相隔的秒数
     *
     * @param preDatetime
     * @param afterDatetime
     * @param hasFloat
     * @return
     * @author 潘深练
     * @Version 1.0 2014-4-18下午01:48:02
     */
    public static float getSecondsByCompare(String preDatetime, String afterDatetime, Boolean hasFloat) {
        return getByCompare(preDatetime, afterDatetime, 4, hasFloat);
    }

    public static Integer getIntSecondsByCompare(String preDatetime, String afterDatetime) {
        Float f = new Float(getByCompare(preDatetime, afterDatetime, 4, false));
        return f.intValue();
    }

    public static float getSecondsByCompare(Date preDatetime, Date afterDatetime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String _preDatetime = df.format(preDatetime);
        String _afterDatetime = df.format(afterDatetime);
        return getSecondsByCompare(_preDatetime, _afterDatetime);
    }

    public static float getSecondsByCompare(String preDatetime, String afterDatetime) {
        return getByCompare(preDatetime, afterDatetime, 4, true);
    }

    public static float CompareDate(String preDatetime, String afterDatetime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date preD = null;
        Date afterD = null;
        Long diff = 0l;
        try {
            preD = df.parse(preDatetime);
            afterD = df.parse(afterDatetime);
            diff = preD.getTime() - afterD.getTime();
        } catch (Exception e) {
            diff = 0l;
        }
        return diff;
    }


    public static float getByCompare(String preDatetime, String afterDatetime,
                                     Integer type, Boolean hasFloat) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date preD = null;
        Date afterD = null;
        try {
            preD = df.parse(preDatetime);
            afterD = df.parse(afterDatetime);
        } catch (Exception e) {
        }
        return getTypeByCompare(preD, afterD, type, hasFloat);
    }

    public static float getTypeByCompare(Date preDate, Date afterDate, Integer type, Boolean hasFloat) {
        if (preDate == null || afterDate == null) {
            return 0l;
        }
        long diff = 0l;
        diff = afterDate.getTime() - preDate.getTime();
        long l = 1l;
        switch (type) {
            case 1:
                l = 1000 * 60 * 60 * 24;
                break;
            case 2:
                l = 1000 * 60 * 60;
                break;
            case 3:
                l = 1000 * 60;
                break;
            case 4:
                l = 1000;
                break;
        }
        if (hasFloat) {
            float a = diff;
            float b = l;
            return a / b;
        } else {
            return diff / l;
        }
    }

    /**
     * 获取系统时间
     */
    public static String getSysDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取系统日期"yyyy-MM-dd"（cyz于20100319增加）
     */
    public static String getSysDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前分钟的数值
     */
    public static int sysMinute() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.MINUTE);
    }

    /**
     * 获取当前分钟的数值
     */
    public static int sysSecond() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的天
     */
    public static String sysHdfsDay() {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatday = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormatday.format(instance.getTime());
    }

    /**
     * 获取当前时间的小时
     */
    public static String sysHdfsHour() {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormathour = new SimpleDateFormat("HH");
        return simpleDateFormathour.format(instance.getTime());
    }

    public static String dateOfMonthLast(String s) {
        Calendar cale = Calendar.getInstance();
        cale.setTime(parse(s, DATE_FULL_FORMAT));
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        String format = format(cale.getTime(), DATE_FULL_FORMAT);
        return format;
    }

    /**
     * 获取当年的第一天
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        currCal.clear();
        currCal.set(Calendar.YEAR, currentYear);
        return currCal.getTime();
    }

    /**
     * 获取当年的最后一天
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        currCal.clear();
        currCal.set(Calendar.YEAR, currentYear);
        currCal.set(Calendar.HOUR, 23);
        currCal.set(Calendar.MINUTE, 59);
        currCal.set(Calendar.SECOND, 59);
        currCal.roll(Calendar.DAY_OF_YEAR, -1);
        return currCal.getTime();
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDay = cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        return isSameYear && isSameMonth && isSameDay;
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     */
    public static boolean isSameDate(String date1, String date2) {
        try {
            Date d1 = org.apache.commons.lang3.time.DateUtils.parseDate(date1, DATE_FULL_FORMAT, DATE_SHORT_FORMAT);
            Date d2 = org.apache.commons.lang3.time.DateUtils.parseDate(date2, DATE_FULL_FORMAT, DATE_SHORT_FORMAT);
            return isSameDate(d1, d2);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 判断两个日期相差多少天
     * differentDays("2018-10-01","2018-10-02","yyyy-MM-dd")  结果：1
     */
    public static int differentDays(String date1, String date2, String dateFormat) {
        Date d1 = parse(date1, dateFormat);
        Date d2 = parse(date2, dateFormat);
        int days = (int) ((d2.getTime() - d1.getTime()) / (1000 * 3600 * 24l));
        return days;
    }


    /**
     * 获取某个日期所在自然周的第一天
     * 注意是自然周，比如传入2018-11-07 00:00:00 则返回2018-11-04 00:00:00
     */
    public static String getFirstDayOfNatureWeek(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(time, DATE_FULL_FORMAT));
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return format(calendar.getTime(), DATE_FULL_FORMAT);
    }

    /**
     * 获得某个日期所在自然周的最后一天
     * 注意是自然周，比如传入2018-11-07 00:00:00 则返回2018-11-10 00:00:00
     */
    public static String getLastDayOfNatureWeek(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(time, DATE_FULL_FORMAT));
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return format(calendar.getTime(), DATE_FULL_FORMAT);
    }

    /**
     * 获取某个日期所在月的第一天
     * 注意是自然周，比如传入2018-11-07 00:00:00 则返回2018-11-01 00:00:00
     */
    public static String getFirstDayOfMonth(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(time, DATE_FULL_FORMAT));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format(calendar.getTime(), DATE_FULL_FORMAT);
    }

    /**
     * 获得某个日期所在月的最后一天
     * 注意是自然周，比如传入2018-11-07 00:00:00 则返回2018-11-30 00:00:00
     */
    public static String getLastDayOfMonth(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(time, DATE_FULL_FORMAT));
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return format(calendar.getTime(), DATE_FULL_FORMAT);
    }

    /**
     * 获取某个时间点， second 秒 以后的日期
     *
     * @param time   time
     * @param second second
     * @return date
     */
    public static String addSecond(String time, Integer second) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, second);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws ParseException {
//		System.out.println(getDaysByCompare("2014-04-18 16:20:52.0","2014-04-18 14:29:12"));
//		System.out.println(getHoursByCompare("2014-04-18 16:20:52.0","2014-04-18 14:29:12"));
//		System.out.println(getMinutesByCompare("2014-04-18 16:20:52.0","2014-04-18 14:29:12"));
//		System.out.println(getSecondsByCompare("2014-04-18 16:20:52.0","2014-04-18 16:29:12"));
//        System.out.println(getMonthsByCompare("2014-02-28","2014-05-27"));
//        System.out.println(getMonthsByCompare("2014-02-28","2014-05-28"));
//        System.out.println(getMonthsByCompare("2014-02-28","2014-05-29"));


//        Date dNow = new Date();   //当前时间
//		Calendar calendar = Calendar.getInstance(); //得到日历
//		//calendar.setTime(dNow);//把当前时间赋给日历
//        System.out.println(calendar.get(Calendar.MINUTE));
//        System.out.println(Calendar.getInstance().get(Calendar.MINUTE)%15 ==0);
//		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
//
//		Date dBefore = calendar.getTime();   //得到前一天的时间
//
//
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
//		String defaultStartDate = sdf.format(dBefore);    //格式化前一天
//
//		String defaultEndDate = sdf.format(dNow); //格式化当前时间
//		System.out.println("前一天的时间是：" + defaultStartDate);
//		System.out.println("生成的时间是：" + defaultEndDate);

//        System.out.println(reduceDay(dNow,4));
//        System.out.println(addDay(dNow,4));
//        System.out.println(addMonth(dNow,4));
        //  System.out.println(getSysDate());


//        System.out.println(parse("2018-03-06 00:00:00",DATE_FULL_FORMAT).getTime());
        Map<String, Integer> lastDataMap = new HashMap<>();
        System.out.println(lastDataMap.get("0000"));
    }
}
