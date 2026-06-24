package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;

import java.util.List;

@Mapper
public interface DD_SOLUTIONData {
    List<DD_SOLUTIONPojo> selectList(@Param(value = "ID") String ID,
                                     @Param(value = "key") String key,
                                     @Param("mindList")List<String> mindList,
                                     @Param(value = "stime") String stime,
                                     @Param(value = "etime") String etime,
                                     @Param(value = "DD_EVALUE") String DD_EVALUE,
                                     @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);
}
