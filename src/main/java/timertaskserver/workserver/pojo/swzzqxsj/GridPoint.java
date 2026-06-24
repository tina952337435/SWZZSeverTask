package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class GridPoint {
    public float lgtd;      // 经度
    public float lttd;      // 纬度
    public float Value;     // 预报值(mm)
    public Date ForecastTime; // 预报时间
    public  int t;           // 时间索引
}
