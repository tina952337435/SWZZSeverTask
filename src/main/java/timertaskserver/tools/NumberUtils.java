package timertaskserver.tools;


/**
 * 这里键入类的描述
 *
 * @author 高起
 * @版权： 版权所有 (c) 2018
 * @see：
 * @创建日期： 2018/12/10 14:49
 * @功能说明：
 */
public class NumberUtils {


    /**
     * .判断字符串是否是数字，是转为Long类型返回，不是返回null
     * @param along .
     * @return java.lang.Long
     * @author 金生明
     * @date 2018/12/10 14:52
     */
    public static Long isNumber(String along) {
        if(ObjUtils.isEmpty(along)){
            return null;
        }
        for (int i = along.length(); --i >= 0; ) {
            int chr = along.charAt(i);
            if (chr < 48 || chr > 57) {
                return null;
            }
        }
        return Long.parseLong(along);
    }

}
