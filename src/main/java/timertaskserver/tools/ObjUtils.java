package timertaskserver.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/***
 * [说明/描述]Object工具类
 *
 * @author 高起
 * @date 2018年7月30日 下午4:19:38
 * @project 项目名称
 * @version 版本号
 * @since 相关/版本
 */
public class ObjUtils {

    private static String serialVersionUID = "serialVersionUID";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String STRING = "java.lang.String";
    private static final String DATE = "java.util.Date";
    private static final String LONG = "java.lang.Long";
    private static final String EMAIL = "eMail";

    /**
     * 对象非空判断 空为true
     *
     * @param
     * @return
     * @author 贾炎锋
     * @date 2018年7月30日
     */
    public static boolean isEmpty(Object obj) {
        if (obj instanceof String) {
            obj = ((String) obj).trim();
        }
        return ObjectUtils.isEmpty(obj);
    }

    /**
     * 对象非空判断 空为true（）
     *
     * @param
     * @return
     * @author bin
     * @date 2018年11月5日
     */
    /**
     * 可定制 非空判断
     * 当传入对象只需一个不为空即满足则allowPartEmpty  为true
     * 如果验证所有入参都不能为空则allowPartEmpty  为false
     *
     * @param allowPartEmpty 是否允许 部分为空
     * @param obj            非空判断对象
     * @return
     */
    public static boolean isEmpty(boolean allowPartEmpty, Object... obj) {
        for (Object o : obj) {
            if (allowPartEmpty && !isEmpty(o)) {
                return false;
            } else if (!allowPartEmpty) {
                if (isEmpty(o)) {
                    return true;
                }
            }
        }
        if (allowPartEmpty) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 集合非空判断
     *
     * @param
     * @return
     * @author bin
     * @date 2018年8月8日
     */
    public static boolean collectionIsNull(Object collection) {
        boolean isNull = false;
        if (isEmpty(collection)) {
            isNull = true;
            return isNull;
        }
        if (collection instanceof Map) {
            Map map = (Map) collection;
            isNull = map.isEmpty();
        } else if (collection instanceof List) {
            List list = (List) collection;
            isNull = list.isEmpty();
        } else if (collection instanceof String[]) {
            String[] list = (String[]) collection;
            if (list != null || list.length > 0) {
                isNull = false;
            }
        } else if (collection instanceof Set) {
            Set set = (Set) collection;
            isNull = set.isEmpty();
        }
        return isNull;
    }


    /**
     * 判断该对象是否: 返回ture表示所有属性为null  返回false表示不是所有属性都是null(暂定)
     *
     * @param obj
     * @return boolean
     * @description
     * @author bin
     * @date 2018年8月8日
     */
    public static boolean isAllFieldNull(Object obj) throws Exception {
        Class stuCla = (Class) obj.getClass();
        //得到属性集合
        Field[] fs = stuCla.getDeclaredFields();
        boolean flag = true;
        for (Field f : fs) {
            if (!f.getName().equals(serialVersionUID)) {
                // 设置属性是可以访问的(私有的也可以)
                f.setAccessible(true);
                String name = f.getName();
                // 得到此属性的值
                Object val = f.get(obj);
                //只要有1个属性不为空,那么就不是所有的属性值都为空
                if (val != null) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 集合去重（如果集合对象是类，那必须重写equals和hashCode方法才有效）
     *
     * @param list 目标对象
     * @author bin
     * @date 2018年9月15日
     */
    public static void removeDuplicate(List list) {
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }


    /**
     * 地址脱敏
     *
     * @param str 目标地址
     * @author bin
     * @date 2018年9月19日
     */
    public static String addressDesensitization(String str) {
        if (!isEmpty(str)) {
            str = str.trim();
            for (int i = 0; i < str.length(); i++) {
                if (Character.isDigit(str.charAt(i))) {
                    str = str.replace(String.valueOf(str.charAt(i)), "*");
                }
            }
        }
        return str;
    }


    /**
     * 证件脱敏
     *
     * @param certNumb 证件编码
     * @param isIDCert 是否为身份证
     * @author bin
     * @date 2018年9月19日
     */
    public static String certificatesDesensitization(Boolean isIDCert, String certNumb) {
        if (ObjUtils.isEmpty(certNumb)) {
            return certNumb;
        }
        if (isIDCert && certNumb.length() >= 15) {
            String var1 = certNumb.substring(0, 6);
            String var2 = certNumb.substring(certNumb.length() - 4, certNumb.length());
            certNumb = var1 + "********" + var2;
        } else if (!isIDCert && certNumb.length() >= 5) {
            String var1 = certNumb.substring(0, 5);
            String var2 = certNumb.substring(certNumb.length() - 1, certNumb.length());
            StringBuilder sb = new StringBuilder(certNumb);
            sb.replace(certNumb.length() - 4, certNumb.length() - 1, "***");
            certNumb = sb.toString();
        }
        return certNumb;
    }


    public static String getSeq() {

        return null;
    }

    /**
     * 覆盖部分属性（适用于patch方式修改数据信息，先查询当前信息，之后再将需要更新的信息重新赋值到当前对象）
     *
     * @param clazz
     * @param nFiled
     */
    public static void setFiledValue(Object clazz, Map<String, Object> nFiled) {
        if (isEmpty(clazz) || collectionIsNull(nFiled)) {
            return;
        }
        Method targetMtd;
        Class cz = clazz.getClass();
        for (String key : nFiled.keySet()) {
            try {
                Field f = cz.getDeclaredField(key.trim());
                Type t = f.getGenericType();
                String mdName = key;
                if (!key.equals(EMAIL)) {
                    mdName = toUpperCaseFirstOne(key);
                }
                switch (t.getTypeName()) {
                    case STRING:
                        targetMtd = clazz.getClass().getMethod("set" + mdName, String.class);
                        if (!isEmpty(nFiled.get(key))) {
                            targetMtd.invoke(clazz, nFiled.get(key).toString());
                        } else {
                            targetMtd.invoke(clazz, new Object[]{null});
                        }
                        break;
                    case LONG:
                        targetMtd = clazz.getClass().getMethod("set" + mdName, Long.class);
                        if (!isEmpty(nFiled.get(key))) {
                            targetMtd.invoke(clazz, Long.valueOf(nFiled.get(key).toString()));
                        } else {
                            targetMtd.invoke(clazz, new Object[]{null});
                        }
                        break;
                    case DATE:
                        targetMtd = clazz.getClass().getMethod("set" + mdName, Date.class);
                        Object object = nFiled.get(key);
                        if (!isEmpty(object)) {
                            Date date;
                            String string = object.toString();
                            //时间戳
                            if (null != NumberUtils.isNumber(string)) {
                                long l = Long.parseLong(string);
                                date = new Date(l);
                            } else {
                                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
                            }
                            targetMtd.invoke(clazz, date);
                        } else {
                            targetMtd.invoke(clazz, new Object[]{null});
                        }
                        break;
                    default:
                        break;
                }


            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    /**
          * 名字脱敏
          * 规则，张三丰，脱敏为：张*丰
          * @param name
          * @return
          */
    public static String nameDesensitization(String name){
        if(name==null || name.isEmpty()){
            return name;
        }
        //String chineseName = "^[\\u4e00-\\u9fa5\\uf900-\\ufa2d·s\\（\\）]{0,50}$";
        //String englishName = "^[A-Za-z\\s]+$";


        String myName = null;
        char[] chars = name.toCharArray();
        if(chars.length==1){
            myName=name;
        }
        if(chars.length==2){
            myName=name.replaceFirst(name.substring(1), "*");
        }
        if(chars.length>2){
            String substring = name.substring(1, chars.length - 1);
            myName=name.replaceAll(name.substring(1, chars.length-1), "*");
        }
        return myName;
    }

    public static String desensitizedName(String fullName){
        if (!(fullName==null || fullName.isEmpty())) {
            if(ObjUtils.isEnglish(fullName) && fullName.length()>3){
                String name = StringUtils.left(fullName, 3);
                return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
            }
            String name = StringUtils.left(fullName, 1);
            return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
        }
        return fullName;
    }

    // 手机号码前三后四脱敏
    public static String mobileEncrypt(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() < 4)) {
            return mobile;
        }
        StringBuilder sb = new StringBuilder(mobile);
        sb.replace(mobile.length() - 4, mobile.length(), "****");
        mobile = sb.toString();
        return mobile;
    }

    // 判断一个字符是不是英文
    //判断是不是英文字母
    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
    /**
     * 新证件脱敏
     *
     * @param certNumb 证件编码
     * @param isIDCert 是否为身份证
     * @author
     * @date 2018年9月19日
     */
    public static String newCertDesensitization(Boolean isIDCert, String certNumb) {
        if (ObjUtils.isEmpty(certNumb)) {
            return certNumb;
        }
        if (isIDCert && certNumb.length() >= 15) {
            String var1 = certNumb.substring(0, 6);
            String var2 = certNumb.substring(certNumb.length() - 2, certNumb.length());
            certNumb = var1 + "**********" + var2;
        } else if (!isIDCert && certNumb.length() > 5) {
            BigDecimal middle = new BigDecimal(certNumb.length()).divide(new BigDecimal(2), 0, BigDecimal.ROUND_HALF_UP);

            String s1 = certNumb.replaceAll("(?<=\\w{" + middle.subtract(new BigDecimal(3)) + "})\\w(?=\\w{" + new BigDecimal(certNumb.length()).subtract(middle).subtract(new BigDecimal(3)) + "})",
                    "*");
            certNumb = s1.toString();
        } else if (!isIDCert && certNumb.length()<= 5){
            certNumb = certNumb.replaceAll(".","*");
        }
        return certNumb;
    }
     public static boolean createFile(String destFileName) {
        File file= new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }if(destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }//判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {//如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
         return true;
     }
    public static void main(String[] args) {
//        System.out.println(certificatesDesensitization(true, "1234"));
//        CustomerDTO customerDTO = new CustomerDTO();
//        customerDTO.setCreateStaff(123L);
//        Map<String, Object> m = new HashMap();
//        m.put("custName", "2");
//        m.put("updateStaff", 3);
//        m.put("updateDate", new Date());
//        setFiledValue(customerDTO, m);
//        System.out.println(customerDTO);
//        String a = "1000|潜在";
//        System.out.println(a.substring(0, a.indexOf("|")));
//        PartyDTO partyDTO = new PartyDTO();
//        partyDTO.setEnglishName("123");
//        partyDTO.setPartyId(1L);
//        Map map = new HashMap();
//        map.put("partyId",null);
//        setFiledValue(partyDTO,map);
//        System.out.println(partyDTO);
//        System.out.println(addressDesensitization("中国100路6层201室"));
//        System.out.println(nameDesensitization("刘博"));
        System.out.println(nameDesensitization("上海高起信息技术有限公司"));
//        System.out.println(desensitizedName("测试用户Rose"));
//        System.out.println(desensitizedName("jackAndRose"));
//        System.out.println(mobileEncrypt("1234"));
        System.out.println(newCertDesensitization(false,"20180531"));
//        System.out.println("============================================");
//        System.out.println(certificatesDesensitization(false,"1234567"));
    }
}


