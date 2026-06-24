package timertaskserver.workserver.data.zjtyphoon;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFLSLJPojo;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFYBLJPojo;

import java.util.List;

@Mapper
public interface ZJ_TFYBLJData {

    Integer insertALL(@Param("zjList") List<ZJ_TFYBLJPojo> zjList);

    Integer deleteALL(@Param("idList") List<String> idList);

    Integer selectMaxId();

    Integer selectCount(@Param("tfId") String id,@Param("time") String time,@Param("ybsj") String ybsj,@Param("tm")String tm);
}
