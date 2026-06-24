package timertaskserver.tools;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

public class FieldIsValid {

    public static <T> T getColumnName(T object,Class<T> objClass) {
        Field[] fields = objClass.getDeclaredFields();
        for(Field field : fields){
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if(!CommonUtills.isEmpty(fieldValue)){
                    boolean value = isValid(fieldValue.toString());
                    if (!value) {
                        return null;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static <T> List<T> getListColumnName(List<T> objectList, Class<T> objClass){
        for(T t : objectList){
            T tobj = getColumnName(t,objClass);
            if(CommonUtills.isEmpty(tobj)){
                return null;
            }
        }
        return objectList;
    }

    /**正则表达式**/
    private static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"+ "(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|drop|execute|ResultSet|where|on|create|connection|statement|jdbcTemplate|all|queryForInt|queryForObject|queryForMap|getConnection|outfile|load_file|join|left|right|between|rownum|dual)\\b)";
    private static Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
    public static boolean isValid(String str) {
        if (sqlPattern.matcher(str).find()){
//            System.out.println("未能通过过滤器：str=" + str);
            return false;
        }
        return true;
    }
}
