package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class St_windyweater_rDto {
    private String TM;
    private Double WIND;
    private String WINDDIRNAME;
    private String WINDZS;
}
