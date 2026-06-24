package timertaskserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzwater.ST_STBPRP_BDto;

import java.util.List;

@Mapper
public interface ST_STBPRP_BData {

    List<ST_STBPRP_BDto> GetSyncSTCD(@Param("stcdList") List<String> stcdList);

    List<ST_STBPRP_BDto> GetSyncSTCDByType(@Param("TYPE")String TYPE,@Param("stcdList") List<String> stcdList);
}
