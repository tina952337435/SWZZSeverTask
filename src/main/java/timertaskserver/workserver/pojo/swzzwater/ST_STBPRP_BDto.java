package timertaskserver.workserver.pojo.swzzwater;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_STBPRP_BDto {
    private String STCD;//
    private String STNM;//
    private String HNNM;//
    private String BSNM;//
    private Double LGTD;//
    private Double LTTD;//
    private String TYPE;//
    private String TM;
    private String ADMAUTH;
    private String SOURCE;
    private String UPZ;
    private String DWZ;
    private String GTOPNUM;
    private String OMCNUM;
    private String SFQ;
    private String ZD;
}
