package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.tools.ParamField;
import timertaskserver.workserver.pojo.swzzqxsj.Tba_weacontentPojo;

import java.util.List;

@Mapper
public interface Tba_weacontentData {

    List<Tba_weacontentPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                        @Param(value = "type")List<String> type,
                                        @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Tba_weacontentPojo bdmsPredictPojo);

    Integer insertOne(Tba_weacontentPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer insertALL(@Param(value = "bpPojo")List<Tba_weacontentPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "bpPojo")List<Tba_weacontentPojo> bpPojo);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
