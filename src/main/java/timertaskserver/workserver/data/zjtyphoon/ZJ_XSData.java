package timertaskserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_XSPojo;

import java.util.List;

@Mapper
public interface ZJ_XSData {
    List<ZJ_XSPojo> selectList(@Param("ptfbh") String ptfbh,@Param("tfbh") String tfbh,@Param("Type")String TYPE);

    Integer insertToOne(ZJ_XSPojo obj);

    Integer upDateToOne(ZJ_XSPojo obj);
}
