package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_windyweater_rPojo {
    private String STCD;
    private String TM;
    private String YBTM;
    private String UPDATETM;
    private Double WIND;
    private Double WINDDIR;
    private Double PRESSURE;
    private Double DRP;
    private Double GUST;
    private Double TEMP;
    private String TYPE;
    private String NOTE;
}
