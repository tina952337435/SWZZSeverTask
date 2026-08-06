package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.StRnflRlistPojo;

import java.util.List;

@Mapper
public interface StRnflRlistData {

    List<StRnflRlistPojo> selectList(@Param(value = "tm") String tm,
                                     @Param(value = "fpdr") Double fpdr);

    Integer insertOne(StRnflRlistPojo pojo);

    Integer deleteByTmAndFpdr(@Param(value = "tm") String tm,
                              @Param(value = "fpdr") Double fpdr);
}
