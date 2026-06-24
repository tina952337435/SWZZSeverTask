package timertaskserver.workserver.pojo.swzzdata;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("SWZZ_TASK_CONFIG")
public class SwzzTaskConfigPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID，如 SynchronizeDataTask
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 任务中文名，如 水位同步
     */
    private String taskName;

    /**
     * 任务类全名
     */
    private String taskClass;

    /**
     * 1=启用 0=禁用
     */
    private Integer enabled;

    /**
     * 执行间隔（分钟）
     */
    private Integer intervalMinutes;

    /**
     * Cron表达式（可选）
     */
    private String cronExpr;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}