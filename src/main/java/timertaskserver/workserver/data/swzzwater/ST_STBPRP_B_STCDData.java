package timertaskserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzwater.ST_STBPRP_B_STCDPojo;

import java.util.List;

@Mapper
public interface ST_STBPRP_B_STCDData {

    List<ST_STBPRP_B_STCDPojo> selectList(@Param("TYPE")String TYPE);

}
