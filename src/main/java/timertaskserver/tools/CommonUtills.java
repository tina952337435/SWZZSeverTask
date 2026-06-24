package timertaskserver.tools;



import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.*;
import java.util.*;

@Component
public class CommonUtills {
    //随机id --start
    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);

    /** This Format for format the data to special format. */
    private final static Format dateFormat = new SimpleDateFormat("MMddHHmmssS");

    /** This Format for format the number to special format. */
    private final static NumberFormat numberFormat = new DecimalFormat("0000");

    /** This int is the sequence number ,the default value is 0. */
    private static int seq = 0;

    private static final int MAX = 9999;
    //随机id --end
    private final String SuccessContent = "操作成功!";

    private final String FailContent = "操作失败!";
    /**
     * 查询是否有数据
     * @param list 传入集合
     * @return 返回成功失败
     *
     */
    public String returnSelect(List list) {
        if (list.size()>0){
            return "true";
        }
        return "false";

    }
    /**
     *
     * @param s 传入int值
     * @return 返回成功失败
     *
     */
    public String returnInOut(int s) {
        if (s>0){
            return "true";
        }
        return "false";

    }

    /**
     * 返回封装
     * @param type 查询请传入select  删除delete 更新update
     * @param resultInt 更新和删除传入数据库Int返回值 大于0 自动返回true
     * @param obj 查询时间返回查询结果给前台
     * @return
     */
    public Map<String,Object> returnJson(String type,int resultInt,Object obj){
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        //创建true或者false
        String resultType="";
        //如果type不是s根据操作返回的数据判断是否成功
            if (resultInt>0){
                resultType= "操作成功";
                objectObjectHashMap.put("IsSuccess",true);
            }else{
                resultType= "操作成功";
                objectObjectHashMap.put("IsSuccess",false);
            }

        //查询返回数据
        if(type.equals("select")){
            objectObjectHashMap.put("Message",resultType);
            objectObjectHashMap.put("data",obj);
            objectObjectHashMap.put("total",getObjectSize(obj));
            return objectObjectHashMap;
        }else{
            //更新和删除返回状态
            objectObjectHashMap.put("Message",resultType);
            objectObjectHashMap.put("content",SuccessContent);
            return objectObjectHashMap;
        }
    }




    /**
     * 返回非标准表格封装
     * @param type 查询请传入select  删除delete 更新update
     * @param resultInt 更新和删除传入数据库Int返回值 大于0 自动返回true
     * @param obj 查询时间返回查询结果给前台 构造表头
     * @param obj 查询时间返回查询结果给前台 构造数据
     * @return
     */
    public Map<String,Object> returnSpecialJson(String type,int resultInt,Object obj,Object obj2){
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        //创建true或者false
        String resultType="";
        //如果type不是s根据操作返回的数据判断是否成功
        if (resultInt>0){
            resultType= "true";
        }else{
            resultType= "false";
        }

        //查询返回数据
        if(type.equals("select")){
            objectObjectHashMap.put("success",resultType);
            objectObjectHashMap.put("data",obj2);
            objectObjectHashMap.put("datatitle",obj);
            objectObjectHashMap.put("total",getObjectSize(obj));
            return objectObjectHashMap;
        }else{
            //更新和删除返回状态
            objectObjectHashMap.put("success",resultType);
            objectObjectHashMap.put("content",SuccessContent);
            return objectObjectHashMap;
        }
    }











    /**
     * 根据object实际参数获取长度
     */
public String getObjectSize(Object obj){
    if (obj.getClass().isArray()){
        int length = Array.getLength(obj);
        return String.valueOf(length);
    }if (obj instanceof List) {
        return String.valueOf(((List) obj).size());
    }if (obj instanceof Set) {
        return String.valueOf(((Set) obj).size());
    }if (obj instanceof Map) {
        return String.valueOf(((Map) obj).size());
    }
    return "未标明的数据格式";
}



    /**
     * 时间格式生成序列
     * @return String
     */
    public static String getUUid() {

        Calendar rightNow = Calendar.getInstance();

        StringBuffer sb = new StringBuffer();

        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);

        numberFormat.format(seq, sb, HELPER_POSITION);

        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
        return sb.toString();
    }


    // 调用http接口获取数据
    public static String getHttpInterface(String path) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URL url = new URL(path);
            //打开和url之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.connect();

            result = new StringBuffer();
            //读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 反射清空实体类
     * @param obj
     */
    @SuppressWarnings("unchecked")
    public static void setObjectFieldsEmpty(Object obj) {
        // 对obj反射
        Class objClass = obj.getClass();
        Method[] objmethods = objClass.getDeclaredMethods();
        Map objMeMap = new HashMap();
        for (int i = 0; i < objmethods.length; i++) {
            Method method = objmethods[i];
            objMeMap.put(method.getName(), method);
        }
        for (int i = 0; i < objmethods.length; i++) {
            {
                String methodName = objmethods[i].getName();
                if (methodName != null && methodName.startsWith("get")) {
                    try {
                        Object returnObj = objmethods[i].invoke(obj,
                                new Object[0]);
                        Method setmethod = (Method) objMeMap.get("set"
                                + methodName.split("get")[1]);
                        if (returnObj != null) {
                            returnObj = null;
                        }
                        setmethod.invoke(obj, returnObj);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合(小时)
     *
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.HOUR_OF_DAY, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }
    /**
     * 根据开始时间和结束时间返回时间段内的时间集合(月)
     * 只保留月日
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<Date> getDatesBetweenTwoDate_MONTH(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }
    /**
     * 根据开始时间和结束时间返回时间段内的时间集合(天)
     * 只保留月日
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<Date> getDatesBetweenTwoDate_Day(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_YEAR, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }
    /**
     * 根据开始时间和结束时间返回时间段内的时间集合(天) 字符串格式
     * 只保留月日
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<String> getBetweenTwoDate_Day_String(Date beginDate, Date endDate) {
        List<String> stringDate = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        stringDate.add(sdf.format(beginDate));
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_YEAR, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                stringDate.add(sdf.format(cal.getTime()));
            } else {
                break;
            }
        }
        stringDate.add(sdf.format(endDate));// 把结束时间加入集合
        return stringDate;
    }




    /**
     * 根据开始时间和结束时间返回时间段内的时间集合(分钟)
     *只保留月日时分
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<Date> getDatesBetweenTwoDate_Month(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }

    /**
     * 格式化开始结束时间  yyyy-MM-dd HH:mm:ss
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public  List<Date> getDataList(String start,String end) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date dBegin = sdf.parse(start);
    Date dEnd = sdf.parse(end);
    List<Date> listDate = getDatesBetweenTwoDate(dBegin, dEnd);


     return  listDate;
}




    /**
     * 对象非空判断 空为true
     *
     * @param
     * @return
     * @author 刘彬
     * @date
     */
    public static boolean isEmpty(Object obj) {
        if (obj instanceof String) {
            obj = ((String) obj).trim();
        }
        return ObjectUtils.isEmpty(obj);
    }


    /**
     * 传入开始结束时间获得多站所需要的时间数组
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public List<String> getDateDuozhan(String start, String end,String type) throws ParseException {
        ArrayList<String> result = new ArrayList<>();
        if(type.equals("hour")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dBegin = sdf.parse(start);
            Date dEnd = sdf.parse(end);
            List<Date> listDate = getDatesBetweenTwoDate(dBegin, dEnd);
            for (int i = 0; i <listDate.size() ; i++) {
                result.add(sdf.format(listDate.get(i)));
            }
        }else if(type.equals("day")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dBegin = sdf.parse(start);
            Date dEnd = sdf.parse(end);
            List<Date> listDate = getDatesBetweenTwoDate_Day(dBegin, dEnd);
            for (int i = 0; i <listDate.size() ; i++) {
                result.add(sdf.format(listDate.get(i)));
            }



        }



        return  result;
    }



    /**
     * 站码排列成in sql 可使用格式
     * @return
     */
    public String getStcds(String stcds){
        String stcd="";
        String[] split = stcds.split(",");
        for (int i = 0; i < split.length; i++) {
            if(i==split.length-1){
                stcd+="'"+split[i]+"'";
            }else{

                stcd+="'"+split[i]+"',";
            }
        }

        return stcd;
    }


}
