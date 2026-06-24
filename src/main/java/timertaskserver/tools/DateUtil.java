package timertaskserver.tools;

import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 高起
 * @version 1.0.0 @版权： 版权所有 (c) 2018
 * @see: @创建日期：2020/6/1
 * @功能说明：自动设置创建、修改时间
 */
public class DateUtil extends DateUtils {
    private static String METHOD_CREATEDATE = "setCreateDate";
    private static String METHOD_UPDATEDATE = "setUpdateDate";
    private static String METHOD_STATUSDATE = "setStatusDate";
    public static int OP_CREATE = 1;
    public static int OP_UPDATE = 2;
    public static int OP_DELETE = 3;
    public static String YMD_HMS = "yyyy-MM-dd hh:mm:ss";
    public static String MDY = "MM/dd/yyyy";
    public static String MDYHMS = "MM/dd/yyyy HH:mm:ss";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYYMMDD = "yyyyMMdd";
    public static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static String YMD = "yyyy-MM-dd";

    /**
     * 根据自定义格式 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateFormat(Date date, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date);
    }

    /**
     * 时间戳格式化
     *
     * @param times
     * @param pattern
     * @return
     */
    public static String longToDate(Long times, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);

        return sf.format(new Date(times));
    }

    /**
     * 时间戳转Date
     *
     * @param times
     * @return
     */
    public static Date longToDate(Long times) {
        return new Date(times);
    }

    /**
     * 时间字符串转Date
     *
     * @param times
     * @return
     */
    public static Date strToDate(String times, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * LocalDateTime转时间字符串(包含日期+时间 如：年月日时分秒)
     *
     * @param times
     * @return
     */
    public static String strToLocalDateTime(LocalDateTime times, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(times);
    }

    /**
     * 时间字符串转LocalDateTime(包含日期+时间 如：年月日时分秒)
     *
     * @param times
     * @return
     */
    public static LocalDateTime localDateTimeToStr(String times, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(times, df);
    }

    /**
     * LocalDate转时间字符串(只有日期 没有时间 如：年月日)
     *
     * @param times
     * @return
     */
    public static String strToLocalDate(LocalDate times, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(times);
    }

    /**
     * 时间字符串转LocalDate(只有日期 没有时间 如：年月日)
     *
     * @param times
     * @return
     */
    public static LocalDate localDateToStr(String times, String pattern) {
        LocalDate beginDateTime = LocalDate.parse(times, DateTimeFormatter.ofPattern(pattern));
        return beginDateTime;
    }

    /**
     * 整编时间为5分钟数据
     *
     * @param times
     * @return
     */
    public static Date pastTM(String times) {
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMS);
        Date date;
        try {
            date = sdf.parse(times);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int minute = calendar.get(Calendar.MINUTE);
            if ((minute % 5) > 0) {
                int yushu = Integer.valueOf(minute / 5);
                return sdf.parse(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + (yushu * 5) + ":00");
            } else {
                return date;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static String pastTM(String times, String pattern) {
        Date tmVal = strToDateTM(times, YMDHMS);
        String valTime = dateToStr(tmVal, pattern);
        return valTime;
    }

    /**
     * 94    * 将短时间格式时间转换为字符串 yyyy-MM-dd
     * 95    *
     * 96    * @param dateDate
     * 97    * @param k
     * 98    * @return
     * 99
     */
    public static String dateToStr(Date dateDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 106    * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 107    *
     * 108    * @param strDate
     * 109    * @return
     * 110
     */
    public static Date strToDateTM(String strDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 返回两个时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param format    日期格式化
     * @param str       返回类型（ d 天、 h 小时、min 分钟）
     * @return
     */
    public static Long dateDiff(String startTime, String endTime,
                                String format, String str) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
//            System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
//                    + (min - day * 24 * 60) + "分钟" + sec + "秒。");
//            System.out.println("hour=" + hour + ",min=" + min);
            if (str.equalsIgnoreCase("d")) {
                return day;
            } else if (str.equalsIgnoreCase("h")) {
                return hour;
            } else {
                return min;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("d")) {
            return day;
        } else if (str.equalsIgnoreCase("h")) {
            return hour;
        } else {
            return min;
        }
    }

    /**
     * 自动添加新增、修改日期(反射)
     *
     * @param opType 新增 ：1 修改：2 删除：3
     * @param clazz  entity
     * @return 默认时间设置
     * @author 杨敏
     * @date 2018年8月8日
     */
    public static void autoSetDateReflect(Object clazz, int opType) throws Exception {
        switch (opType) {
            case 1: {
                //新建时添加 创建时间、修改时间、状态修改时间
                setDateValue(clazz, METHOD_CREATEDATE, METHOD_UPDATEDATE, METHOD_STATUSDATE);
                break;
            }
            case 2: {
                //修改时更新 修改时间
                setDateValue(clazz, METHOD_UPDATEDATE);
                break;
            }
            case 3: {
                //逻辑删除时更新 修改时间、状态修改时间
                setDateValue(clazz, METHOD_UPDATEDATE, METHOD_STATUSDATE);
                break;
            }
        }
    }

    /**
     * 遍历方法设值
     *
     * @param clazz
     * @param methodNames
     */
    private static void setDateValue(Object clazz, String... methodNames) {
        Method targetMtd = null;
        for (String mdName : methodNames) {
            try {
                targetMtd = clazz.getClass().getMethod(mdName, Date.class);
                targetMtd.invoke(clazz, new Date());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取某天的时间
     *
     * @param simpleDateFormat 日期格式化
     * @param index            为正表示当前时间加天数，为负表示当前时间减天数
     * @return
     */
    public static String addDay(String simpleDateFormat, int index) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat((!ObjUtils.isEmpty(simpleDateFormat) ? simpleDateFormat : YMDHMS));
        calendar.add(Calendar.DAY_OF_MONTH, index);
        String date = fmt.format(calendar.getTime());
        return date;
    }
    /**
     * 获取某小时的时间
     *
     * @param dateformat 日期格式化
     * @param index            为正表示当前时间加天数，为负表示当前时间减天数
     * @return
     */
    public static String addHourDay(String date, int index, String dateformat){
        String reStr ="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
            Date dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.HOUR, index);
            Date dt1 = rightNow.getTime();
            reStr = sdf.format(dt1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return reStr;
    }

    /****
     * 传入具体日期 ，返回具体日期减少一天
     * @param date 日期(2017-04-13)
     * @return 2017-04-12
     * @throws ParseException
     */
    public static String addDay(String date, int index, String dateformat){
        String reStr ="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
            Date dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_MONTH, index);
            Date dt1 = rightNow.getTime();
            reStr = sdf.format(dt1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return reStr;
    }

    /**
     * 获取未来某一年的日期
     *
     * @return
     */
    public static Date getCustomerDate() {
        Calendar canlandar = Calendar.getInstance();
        canlandar.setTime(new Date());
        canlandar.add(Calendar.YEAR, 60);
        return canlandar.getTime();
    }

    /**
     * 获取时间所在月第一天和最后一天
     *
     * @param start_time
     * @param num        0-第一天  1-最后一天
     * @return
     */
    public static String getEndDayOrOneDay(Date start_time, int num) {
        String time = "";
        Calendar cale = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        if (num == 0) {
            // 获取前月的第一天
            cale = Calendar.getInstance();
            cale.setTime(start_time);
            cale.add(Calendar.MONTH, 0);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            time = format.format(cale.getTime());
        } else {
            // 获取前月的最后一天
            cale = Calendar.getInstance();
            cale.setTime(start_time);
            cale.add(Calendar.MONTH, 1);
            cale.set(Calendar.DAY_OF_MONTH, 0);
            time = format.format(cale.getTime());
        }
        SimpleDateFormat fmt = new SimpleDateFormat(YMDHMS);
        String date = fmt.format(cale.getTime());
        return date;
    }

    public static void main(String[] args) {
        LocalDateTime localDate = LocalDateTime.now();
        System.out.println(strToLocalDateTime(localDate, YYYYMMDDHHMMSS));

        System.out.println(localDateTimeToStr("20190706155412", YYYYMMDDHHMMSS).minusMonths(1));
    }
}
