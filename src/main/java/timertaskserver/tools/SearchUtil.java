package timertaskserver.tools;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

/**
 * @author lk
 * @Date 2020-06-16 20:24
 * @Description
 */
public class SearchUtil {
    public static QueryWrapper parseWhereSql(String conditionJson){
        QueryWrapper queryWrapper = new QueryWrapper();
        if(!ObjUtils.isEmpty(conditionJson)){
            List<ConditionVo> conditionList = JSON.parseArray(conditionJson,ConditionVo.class);
            if(!ObjUtils.collectionIsNull(conditionList)){
                for(ConditionVo conditionVo : conditionList){
                    switch (conditionVo.getType()){
                        case "eq": queryWrapper.eq(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "ne": queryWrapper.ne(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "like": queryWrapper.like(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "leftlike": queryWrapper.likeLeft(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "rightlike": queryWrapper.likeRight(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "notlike": queryWrapper.notLike(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "gt": queryWrapper.gt(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "lt": queryWrapper.lt(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "ge": queryWrapper.ge(conditionVo.getColumn(),conditionVo.getValue());break;
                        case "le": queryWrapper.le(conditionVo.getColumn(),conditionVo.getValue());break;
                    }
                }
            }
        }
        return queryWrapper;
    }

    public static <T> QueryWrapper<T> parseWhereSql(List<ConditionVo> conditionList, T type) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(!ObjUtils.collectionIsNull(conditionList)){
            for(ConditionVo conditionVo : conditionList){
                switch (conditionVo.getType()){
                    case "eq": queryWrapper.eq(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "ne": queryWrapper.ne(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "like": queryWrapper.like(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "leftlike": queryWrapper.likeLeft(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "rightlike": queryWrapper.likeRight(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "notlike": queryWrapper.notLike(conditionVo.getColumn(),conditionVo.getValue());break;
                    case "gt":
                        if(conditionVo.getColumn().equals("stime"))
                            queryWrapper.apply(" tm > to_date({0},'yyyy-mm-dd hh24:mi:ss')",conditionVo.getValue());
                        else
                            queryWrapper.gt(conditionVo.getColumn(),conditionVo.getValue());
                        break;
                    case "lt":
                        if(conditionVo.getColumn().equals("etime"))
                            queryWrapper.apply(" tm < to_date({0},'yyyy-mm-dd hh24:mi:ss')",conditionVo.getValue());
                        else
                            queryWrapper.lt(conditionVo.getColumn(),conditionVo.getValue());
                        break;
                    case "ge":
                        if(conditionVo.getColumn().equals("stime"))
                            queryWrapper.apply(" tm >= to_date({0},'yyyy-mm-dd hh24:mi:ss')",conditionVo.getValue());
                        else
                            queryWrapper.ge(conditionVo.getColumn(),conditionVo.getValue());
                        break;
                    case "le":
                        if(conditionVo.getColumn().equals("etime"))
                            queryWrapper.apply( "tm <= to_date({0},'yyyy-mm-dd hh24:mi:ss')",conditionVo.getValue());
                        else
                            queryWrapper.le(conditionVo.getColumn(),conditionVo.getValue());
                        break;
                    case "apply": queryWrapper.apply(""+conditionVo.getColumn()+" = to_date({0},'yyyy-mm-dd hh24:mi:ss')",conditionVo.getValue());break;
                }
            }
        }
        return  queryWrapper;
    }

    /**
     * 获取自定义参数（非实体类参数：例如 STIEM、ETIME（type参数给空））
     * @param conditionList
     * @param name
     * @return
     */
    public static String getValue(List<ConditionVo> conditionList,String name){
        String value = "";
        if(!ObjUtils.collectionIsNull(conditionList)) {
            for(ConditionVo conditionVo : conditionList){
                if(conditionVo.getColumn().equals(name)){
                    value = conditionVo.getValue();
                    break;
                }
            }
        }
        return  value;
    }

}
