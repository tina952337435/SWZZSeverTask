package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_areatide_rybPojo {
    private String ID;
    private Double MINWAVEHEIGHT;
    private Double MAXWAVEHEIGHT;
    private Double MINSEATEMP;
    private Double MAXSEATEMP;
    private String MINTIDEDATE;
    private Double MINTIDE;
    private String MAXTIDEDATE;
    private Double MAXTIDE;
    private Double AGING;
    private String UPDATEDATE;
    private String UNIT;
    private String AREANAME;
    private String AREAID;
}
