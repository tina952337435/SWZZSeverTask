package timertaskserver.workserver.data.swzzqxsj;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzqxsj.Tz_watersheddataPojo;


import java.util.List;

@Mapper
public interface Tz_watersheddataData {

    List<Tz_watersheddataPojo> selectList(@Param(value = "ID") String ID, @Param(value = "key") String key,
                                          @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                                          @Param(value = "type")List<String> type,
                                          @Param(value = "startindex") Integer startindex, @Param(value = "pagesize") Integer pagesize);

    Integer updateOne(Tz_watersheddataPojo bdmsPredictPojo);

    Integer insertOne(Tz_watersheddataPojo bdmsPredictPojo);

    Integer deleteOne(@Param(value = "KEYID") String KEYID,@Param(value = "FTM") String FTM,@Param(value = "RLSTM") String RLSTM,@Param(value = "FPDR") Double FPDR,@Param(value = "TYPE") String TYPE);

    Integer insertALL(@Param(value = "bpPojo")List<Tz_watersheddataPojo> bpPojo);

    Integer deleteALL(@Param(value = "bpPojo")List<Tz_watersheddataPojo> bpPojo);

    List<Tz_watersheddataPojo> selectByTimeAndFPDR(@Param(value = "stime") String stime,
                                                   @Param(value = "etime") String etime,
                                                   @Param(value = "rlstime") String rlstime,
                                                   @Param(value = "rletime") String rletime,
                                                   @Param(value = "fpdr") List<String> fpdr);

    Integer updateALL(@Param(value = "bpPojo")List<Tz_watersheddataPojo> bpPojo);

    Integer selectCount(@Param(value = "ID") String ID, @Param(value = "key") String key,
                        @Param(value = "stime") String stime, @Param(value = "etime") String etime,
                        @Param(value = "type")List<String> type);
}
