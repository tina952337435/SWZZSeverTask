package timertaskserver.tools;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 公共字段类
 */
@Component
@Data
public class ParamField {
    //开始时间
    private String startdate;
    //结束时间
    private String enddate;
    //关键字
    private String kwtxt;
    //编号
    private String stcd;
    //类型
    private String pattem;
    //分码
    private String pageindex;
    //每页条数
    private String pagesize;
    //备用
    private String strExp;
    //主键1
    private String kID1;
    //主键2
    private String kID2;
    //主键3
    private String kID3;
    private String year;
    private String ddwj;
}
