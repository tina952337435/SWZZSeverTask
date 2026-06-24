package timertaskserver.workserver.pojo.swzzwater;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ST_GATE_RPojo {
    private String STCD;
    private String TM;
    private String EXKEY;
    private String EQPTP;
    private String EQPNO;
    private Double GTOPNUM;
    private Double GTOPHGT;
    private Double GTQ;
    private String MSQMT;
}
