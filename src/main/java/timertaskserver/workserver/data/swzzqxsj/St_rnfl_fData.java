package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.tools.ParamField;
import timertaskserver.workserver.pojo.swzzqxsj.St_rnfl_fPojo;


import java.util.List;

@Mapper
public interface St_rnfl_fData {

    List<St_rnfl_fPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                   @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                   @Param(value = "type")List<String> type,
                                   @Param(value = "ybtm") String ybtm,
                                   @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(St_rnfl_fPojo bdmsPredictPojo);

    Integer insertOne(St_rnfl_fPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "ID") String ID,@Param(value = "YBTM")String YBTM,@Param(value = "TM")String TM);

    Integer insertALL(@Param(value = "bpPojo")List<St_rnfl_fPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<ParamField> bpPojo);

    List<St_rnfl_fPojo> selectByHourHX(@Param(value = "stime") String stime,
                                                   @Param(value = "etime") String etime,
                                                   @Param(value = "rlstime") String rlstime,
                                                   @Param(value = "rletime") String rletime,
                                                   @Param(value = "hour") List<String> fpdr);

    Integer updateALL(@Param(value = "list")List<St_rnfl_fPojo> list);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type,
                        @Param(value = "ybtm") String ybtm
                        );
    St_rnfl_fPojo selectByYBTMNew();

    St_rnfl_fPojo selectByYBTMNewByStcd(@Param(value = "stcd") String stcd);

}
