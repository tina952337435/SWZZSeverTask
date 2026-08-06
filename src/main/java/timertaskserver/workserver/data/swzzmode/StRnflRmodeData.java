package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.StRnflRmodePojo;

import java.util.List;

@Mapper
public interface StRnflRmodeData {

    List<StRnflRmodePojo> selectByYbtmAndFpdr(@Param(value = "ybtm") String ybtm,
                                               @Param(value = "fpdr") Double fpdr,
                                               @Param(value = "type") String type);

    Integer insertALL(@Param(value = "objList") List<StRnflRmodePojo> objList);

    Integer updateDrp(StRnflRmodePojo pojo);

    Integer deleteByYbtmAndFpdr(@Param(value = "ybtm") String ybtm,
                                @Param(value = "fpdr") Double fpdr,
                                @Param(value = "type") String type);
}
