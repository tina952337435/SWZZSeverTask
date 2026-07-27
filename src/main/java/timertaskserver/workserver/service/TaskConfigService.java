package timertaskserver.workserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import timertaskserver.workserver.data.swzzdata.SwzzTaskConfigData;
import timertaskserver.workserver.pojo.swzzdata.SwzzTaskConfigPojo;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskConfigService {

    @Autowired
    private SwzzTaskConfigData taskConfigData;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 根据配置启动单个任务
     */
    public boolean startTask(SwzzTaskConfigPojo config) {
        if (config.getEnabled() == 0) {
            System.out.println("[TaskConfigService] 任务 " + config.getId() + " 已禁用，不启动");
            return false;
        }

        try {
            Class<?> taskClass = Class.forName(config.getTaskClass());
            Object task = taskClass.newInstance();

            // 1. 先取消旧任务，避免新旧同时运行
            TaskRegistry.unregister(config.getTaskClass());

            // 2. 用 MutexTaskWrapper 包装，统一给所有任务加互斥锁
            Runnable wrappedTask = new MutexTaskWrapper((Runnable) task);

            ScheduledFuture<?> future;
            if (config.getCronExpr() != null && !config.getCronExpr().isEmpty()) {
                future = threadPoolTaskScheduler.schedule(wrappedTask, new CronTrigger(config.getCronExpr()));
            } else {
                long intervalMillis = config.getIntervalMinutes() * 60 * 1000L;
                future = threadPoolTaskScheduler.scheduleAtFixedRate(
                        wrappedTask,
                        Instant.now().plusMillis(2000),  // 延迟2秒启动，给 cancel 旧任务留出时间
                        Duration.ofMillis(intervalMillis)
                );
            }

            // 3. 注册新任务
            TaskRegistry.register(config.getTaskClass(), future);
            System.out.println("[TaskConfigService] 启动任务: " + config.getTaskClass() + " (" + config.getTaskName() + ")");
            return true;

        } catch (Exception e) {
            System.err.println("[TaskConfigService] 启动任务失败: " + config.getTaskClass() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 停止单个任务
     */
    public boolean stopTask(String taskId) {
        // 用 DB ID 查出全类名，以匹配 TaskRegistry 的 key
        SwzzTaskConfigPojo config = taskConfigData.selectById(taskId);
        if (config == null) {
            return false;
        }
        boolean result = TaskRegistry.unregister(config.getTaskClass());
        if (result) {
            System.out.println("[TaskConfigService] 停止任务: " + taskId);
        }
        return result;
    }

    /**
     * 启用任务（从数据库读取最新配置）
     */
    public boolean enableTask(String taskId) {
        SwzzTaskConfigPojo config = taskConfigData.selectById(taskId);
        if (config == null) {
            System.err.println("[TaskConfigService] 任务配置不存在: " + taskId);
            return false;
        }
        config.setEnabled(1);
        taskConfigData.updateEnabled(taskId, 1);
        return startTask(config);
    }

    /**
     * 禁用任务
     */
    public boolean disableTask(String taskId) {
        boolean stopped = stopTask(taskId);
        taskConfigData.updateEnabled(taskId, 0);
        return stopped;
    }

    /**
     * 获取所有任务配置
     */
    public List<SwzzTaskConfigPojo> getAllConfigs() {
        return taskConfigData.selectAll();
    }

    /**
     * 获取启用的任务配置
     */
    public List<SwzzTaskConfigPojo> getEnabledConfigs() {
        return taskConfigData.selectEnabled();
    }

    /**
     * 根据ID获取单个任务配置
     */
    public SwzzTaskConfigPojo getConfigById(String taskId) {
        return taskConfigData.selectById(taskId);
    }

    /**
     * 保存任务配置
     */
    public int saveConfig(SwzzTaskConfigPojo config) {
        return taskConfigData.saveOrUpdate(config);
    }

    /**
     * 批量启动所有启用的任务
     */
    public int startAllEnabledTasks() {
        List<SwzzTaskConfigPojo> enabledTasks = taskConfigData.selectEnabled();
        int successCount = 0;

        for (SwzzTaskConfigPojo config : enabledTasks) {
            if (startTask(config)) {
                successCount++;
            }
        }

        System.out.println("[TaskConfigService] 批量启动完成，成功: " + successCount + "/" + enabledTasks.size());
        return successCount;
    }

    /**
     * 检查任务是否正在运行
     */
    public boolean isTaskRunning(String taskId) {
        SwzzTaskConfigPojo config = taskConfigData.selectById(taskId);
        if (config == null) {
            return false;
        }
        return TaskRegistry.isRunning(config.getTaskClass());
    }

    /**
     * 获取正在运行的任务数量
     */
    public int getRunningTaskCount() {
        return TaskRegistry.size();
    }
}
