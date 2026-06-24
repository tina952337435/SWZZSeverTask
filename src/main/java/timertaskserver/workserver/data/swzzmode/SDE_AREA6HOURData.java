package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.SDE_AREA6HOURPojo;

import java.util.List;

@Mapper
public interface SDE_AREA6HOURData {

    List<SDE_AREA6HOURPojo> selectList(@Param(value = "ID") Integer ID,
                                       @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(SDE_AREA6HOURPojo sdeAreaPojo);

    Integer insertOne(SDE_AREA6HOURPojo sdeAreaPojo);

    Integer deleteOne(@Param(value = "ID") Integer ID);

    Integer insertALL(@Param(value = "objList") List<SDE_AREA6HOURPojo> objList);

    Integer selectCount(@Param(value = "ID") Integer ID);
}
