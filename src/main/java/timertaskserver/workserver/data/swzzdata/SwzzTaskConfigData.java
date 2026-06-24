package timertaskserver.workserver.data.swzzdata;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import timertaskserver.workserver.pojo.swzzdata.SwzzTaskConfigPojo;

import java.util.List;

@Mapper
public interface SwzzTaskConfigData {

    /**
     * 查询所有任务配置
     */
    List<SwzzTaskConfigPojo> selectAll();

    /**
     * 查询启用的任务配置
     */
    List<SwzzTaskConfigPojo> selectEnabled();

    /**
     * 根据ID查询单个任务配置
     */
    SwzzTaskConfigPojo selectById(@Param("id") String id);

    /**
     * 更新任务启用状态
     */
    int updateEnabled(@Param("id") String id, @Param("enabled") Integer enabled);

    /**
     * 更新任务间隔
     */
    int updateInterval(@Param("id") String id, @Param("intervalMinutes") Integer intervalMinutes);

    /**
     * 保存或更新任务配置
     */
    int saveOrUpdate(SwzzTaskConfigPojo config);
}