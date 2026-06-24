package timertaskserver.workserver.pojo.swzzwater;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_STBPRP_B_STCDPojo {
    private String STCD;
    private String TYPE;
    private String SOURCE;
    private String STNM;
    private String ZSTCD;
    private String UPZ;
    private String DWZ;
    private String ZTM;
    private String ZD;
    private Integer TIMEINTERVAL;
    private String ISSTATUS;
    private String LASTTM;
    private String REMARK;
}
