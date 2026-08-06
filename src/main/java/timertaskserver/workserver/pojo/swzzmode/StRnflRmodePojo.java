package timertaskserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class StRnflRmodePojo {
    private String STCD;        // STCD (区域名称，如"浦东新区")
    private String YBTM;        // YBTM (预报起报时间)
    private String TM;          // TM (预报时间)
    private Double DRP;         // DRP (降雨量)
    private Double INTV;        // INTV (间隔)
    private Double TEMP;        // TEMP
    private Double HUMIDITY;    // HUMIDITY
    private String WINDDIR;     // WINDDIR
    private Double WINDSPEED;   // WINDSPEED
    private String WEATHERCODE; // WEATHERCODE
    private Double AIRPRESSURE; // AIRPRESSURE
    private String TYPE;        // TYPE ("行政分区"/"水利分片")
    private Double FPDR;        // FPDR (6或48)
}
