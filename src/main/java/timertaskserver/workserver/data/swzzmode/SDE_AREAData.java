package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.SDE_AREAPojo;


import java.util.List;

@Mapper
public interface SDE_AREAData {

    List<SDE_AREAPojo> selectList(@Param(value = "ID") Integer ID,
                                  @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(SDE_AREAPojo sdeAreaPojo);

    Integer insertOne(SDE_AREAPojo sdeAreaPojo);

    Integer deleteOne(@Param(value = "ID") Integer ID);

    Integer insertALL(@Param(value = "objList") List<SDE_AREAPojo> objList);

    Integer selectCount(@Param(value = "ID") Integer ID);
}
