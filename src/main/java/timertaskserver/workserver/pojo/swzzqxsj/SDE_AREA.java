package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@Data

public class SDE_AREA {
    private  Integer FID;           // 区域ID
    private String AREANAME;       // 分区名称
    private float AREAMEASURE; // 面积
    private float COLOR;      // 颜色值
    private float ZVALUE;     // Z值
}
