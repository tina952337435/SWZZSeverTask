package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.tools.ParamField;
import timertaskserver.workserver.pojo.swzzqxsj.St_tide_rybPojo;


import java.util.List;

@Mapper
public interface St_tide_rybData {

    List<St_tide_rybPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                     @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                     @Param(value = "type")List<String> type,
                                     @Param(value = "ybstime") String ybstime, @Param(value = "ybetime") String ybetime,
                                     @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_tide_rybPojo bdmsPredictPojo);

    Integer insertOne(St_tide_rybPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "YBTM")String YBTM,@Param(value = "TM")String TM);

    Integer insertALL(@Param(value = "bpPojo")List<St_tide_rybPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    List<St_tide_rybPojo> selectListByNew(@Param(value = "ID") String ID,
                                          @Param(value = "stime") String stime, @Param(value = "etime") String etime);

    Integer updateALL(@Param(value = "bpPojo")List<St_tide_rybPojo> bpPojo);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type,
                        @Param(value = "ybstime") String ybstime, @Param(value = "ybetime") String ybetime
                        );

    List<St_tide_rybPojo> selectByNewTm(@Param(value = "ID") String stcd);
}
