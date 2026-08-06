package timertaskserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class StRnflRlistPojo {
    private String ID;      // ID
    private String NCFILE;  // NCFILE
    private String TM;      // TM (起报时间)
    private Double FPDR;    // FPDR (6或48)
}
