package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
public class GridParam {
    public int num;        // 网格编号
    public float lgtd;     // 经度
    public float lttd;     // 纬度
    public List<List<Double>> geometry; // 几何坐标(嵌套列表)
    public float drp;      // 降水量
}
