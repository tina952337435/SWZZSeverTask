package timertaskserver.workserver.data.swzzwater;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzwater.RTSQBZKDPojo;

import java.util.List;

@Mapper
public interface RTSQBZKDData {

    List<RTSQBZKDPojo> selectList(@Param("idList") List<String> idList,
                                  @Param("stime") String stime,
                                  @Param("etime") String etime);
}
