package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Data
public class St_rnfl_fPojo {
    private String STCD;
    private String YBTM;
    private String TM;
    private Double DRP;
    private Double INTV;
    private Double TEMP;
    private Double HUMIDITY;
    private String WINDDIR;
    private Double WINDSPEED;
    private String WEATHERCODE;
    private Double AIRPRESSURE;
    private String TYPE;

    public boolean equals(Object o){
        if (o instanceof St_rnfl_fPojo){
            St_rnfl_fPojo m = (St_rnfl_fPojo)o;
            return this.getSTCD().equals(m.getSTCD())
                    && this.getYBTM().equals(m.getYBTM())
                    && this.getTM().equals(m.getTM());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(STCD, YBTM, TM);
    }
}
