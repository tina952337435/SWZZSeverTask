package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.Tz_watershedwgPojo;


import java.util.List;

@Mapper
public interface Tz_watershedwgData {

    List<Tz_watershedwgPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                        @Param(value = "type")List<String> type,
                                        @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Tz_watershedwgPojo bdmsPredictPojo);

    Integer insertOne(Tz_watershedwgPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "TYPE") String TYPE);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
