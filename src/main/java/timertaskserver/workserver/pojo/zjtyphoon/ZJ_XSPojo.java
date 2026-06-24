package timertaskserver.workserver.pojo.zjtyphoon;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZJ_XSPojo {
    private Double PTFBH;
    private Double TFBH;
    private String TYPE;
    private String TFSTIME;
    private String TFETIME;
    private String TFNOTE;
    private Double TFXIANGSIDU;
    private String ISDEL;
}
