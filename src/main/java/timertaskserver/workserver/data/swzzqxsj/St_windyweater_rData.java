package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.tools.ParamField;
import timertaskserver.workserver.pojo.swzzqxsj.St_windyweater_rPojo;


import java.util.List;

@Mapper
public interface St_windyweater_rData {

    List<St_windyweater_rPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                          @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                          @Param(value = "type")List<String> type,
                                          @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_windyweater_rPojo bdmsPredictPojo);

    Integer insertOne(St_windyweater_rPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "YBTM")String YBTM,@Param(value = "TM")String TM);

    Integer insertALL(@Param(value = "bpPojo")List<St_windyweater_rPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    Integer updateALL(@Param(value = "bpPojo")List<St_windyweater_rPojo> bpPojo);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);

    Integer deleteALLBySTCDAndYBTM(@Param(value = "stcd")String stcd,@Param(value = "ybtm")String ybtm);
}
