package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.Tz_ncfilelistPojo;


import java.util.List;

@Mapper
public interface Tz_ncfilelistData {

    List<Tz_ncfilelistPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                       @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                       @Param(value = "type")List<String> type,
                                       @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Tz_ncfilelistPojo bdmsPredictPojo);

    Integer insertOne(Tz_ncfilelistPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer insertAll(@Param(value = "objList")List<Tz_ncfilelistPojo> list);
}
