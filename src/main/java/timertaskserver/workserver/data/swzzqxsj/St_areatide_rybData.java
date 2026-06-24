package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.St_areatide_rybPojo;


import java.util.List;

@Mapper
public interface St_areatide_rybData {

    List<St_areatide_rybPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                         @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                         @Param(value = "type")List<String> type,
                                         @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_areatide_rybPojo bdmsPredictPojo);

    Integer insertOne(St_areatide_rybPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
