package timertaskserver.workserver.pojo.swzzqxsj;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Data
public class Tz_watersheddataPojo {
    private String KEYID;
    private String FTM;
    private String RLSTM;
    private Double FPDR;
    private Double DRP;
    private String TYPE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tz_watersheddataPojo that = (Tz_watersheddataPojo) o;
        return KEYID.equals(that.KEYID) &&
                FTM.equals(that.FTM) &&
                RLSTM.equals(that.RLSTM) &&
                FPDR.equals(that.FPDR) &&
                TYPE.equals(that.TYPE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(KEYID, FTM, RLSTM, FPDR, TYPE);
    }
}

