package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.Tz_watershedPojo;


import java.util.List;

@Mapper
public interface Tz_watershedData {

    List<Tz_watershedPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                      @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                      @Param(value = "type")List<String> type,
                                      @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Tz_watershedPojo bdmsPredictPojo);

    Integer insertOne(Tz_watershedPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
