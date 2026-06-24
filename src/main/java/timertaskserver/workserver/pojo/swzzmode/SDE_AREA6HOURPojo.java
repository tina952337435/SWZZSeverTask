package timertaskserver.workserver.pojo.swzzmode;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SDE_AREA6HOURPojo {
    private Integer FID;
    private String AREANAME;
    private Double AREAMEASURE;
    private Double COLOR;
    private Double ZVALUE;
}
