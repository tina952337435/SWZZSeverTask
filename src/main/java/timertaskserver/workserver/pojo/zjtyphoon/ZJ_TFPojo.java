package timertaskserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_TFPojo {
    private Integer ZJ_ID;
    private String ZJ_TFBH;
    private String ZJ_TFM;
    private String ZJ_TFME;
    private String ZJ_TFLAND;
    private String ZJ_TFDATE;
    private Integer ZJ_BEDIT;
    private Integer ZJ_ISCOMPLETED;
    private String ZJ_REMARK;
    private Integer ZT_ZJ_YDSD;
    private Integer ZJ_SevenRadius;
    private Integer ZJ_TenRadius;
    private Integer ZJ_Grade;
}
