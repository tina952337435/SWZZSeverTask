package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.TzgriddataPojo;

import java.util.List;

@Mapper
public interface TzgriddataData {

    List<TzgriddataPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                    @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                    @Param(value = "type")List<String> type,
                                    @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(TzgriddataPojo bdmsPredictPojo);

    Integer insertOne(TzgriddataPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "GRIDCODE") String GRIDCODE,@Param(value = "FTM") String FTM,@Param(value = "RLSTM") String RLSTM);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
