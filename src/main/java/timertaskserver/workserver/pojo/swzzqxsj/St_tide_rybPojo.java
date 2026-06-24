package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_tide_rybPojo {
    private String STCD;
    private String TM;
    private String YBTM;
    private Double TDZ;
    private String RTYPE;
    private String NOTE;
    private String REMARK;
}
