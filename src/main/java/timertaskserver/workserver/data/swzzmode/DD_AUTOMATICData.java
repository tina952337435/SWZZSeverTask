package timertaskserver.workserver.data.swzzmode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import timertaskserver.workserver.pojo.swzzmode.DD_AUTOMATICPojo;

import java.util.List;

@Mapper
public interface DD_AUTOMATICData {
    List<DD_AUTOMATICPojo> selectList(@Param(value = "ID") String ID,@Param(value = "ISJS") String ISJS,@Param(value = "startindex") Integer startindex,@Param(value = "pagesize") Integer pagesize);
}
