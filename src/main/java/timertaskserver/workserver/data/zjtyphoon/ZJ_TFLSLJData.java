package timertaskserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFLSLJPojo;

import java.util.List;

@Mapper
public interface ZJ_TFLSLJData {

    Integer insertALL(@Param("zjList") List<ZJ_TFLSLJPojo> zjList);

    Integer deleteALL(@Param("idList") List<String> idList);

    Integer selectMaxId();

    Integer selectCount(@Param("tfId") String id,@Param("time") String time);

    List<ZJ_TFLSLJPojo> selectList(@Param("tfbh")String tfbh);
}
