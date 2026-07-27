package timertaskserver.workserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
public class MyEnableScheduledService {

    @Autowired
    @Qualifier("MyThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public boolean startTimerTask(Runnable runTask, String cron) {
        String taskKey = runTask.getClass().getName();
        try {
            // 1. 先取消旧任务
            TaskRegistry.unregister(taskKey);

            // 2. 包装后再调度
            Runnable wrappedTask = new MutexTaskWrapper(runTask);
            CronTrigger cronTrigger = new CronTrigger(cron);
            ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(wrappedTask, cronTrigger);

            // 3. 注册新任务
            TaskRegistry.register(taskKey, future);
            System.out.println("[MyEnableScheduledService] 启动任务(cron): " + taskKey);
            return true;
        } catch (Exception e) {
            System.err.println("[MyEnableScheduledService] 启动任务失败(cron): " + taskKey + " - " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean showDownTimerTask(String className) {
        boolean result = TaskRegistry.unregister(className);
        if (result) {
            System.out.println("[MyEnableScheduledService] 停止任务: " + className);
        }
        return result;
    }

    public boolean restartTimerTask(String className, String cron) {
        try {
            // 先取消旧任务
            TaskRegistry.unregister(className);

            // 通过类名反射创建新实例
            Class<?> taskClass = Class.forName(className);
            Runnable runnable = (Runnable) taskClass.newInstance();
            Runnable wrappedTask = new MutexTaskWrapper(runnable);

            ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(wrappedTask, new CronTrigger(cron));
            TaskRegistry.register(className, future);
            System.out.println("[MyEnableScheduledService] 重启任务: " + className);
            return true;
        } catch (Exception e) {
            System.err.println("[MyEnableScheduledService] 重启任务失败: " + className + " - " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean startTimerTask(Runnable runTask, long intervalMinutes) {
        String taskKey = runTask.getClass().getName();
        try {
            // 1. 先取消旧任务，避免新旧同时运行
            TaskRegistry.unregister(taskKey);

            // 2. 包装后再调度
            Runnable wrappedTask = new MutexTaskWrapper(runTask);
            long intervalMillis = intervalMinutes * 60 * 1000;
            ScheduledFuture<?> future = threadPoolTaskScheduler.scheduleAtFixedRate(
                    wrappedTask,
                    Instant.now().plusMillis(2000),  // 延迟2秒启动，给 cancel 旧任务留出时间
                    Duration.ofMillis(intervalMillis)
            );

            // 3. 注册新任务
            TaskRegistry.register(taskKey, future);
            System.out.println("[MyEnableScheduledService] 启动任务(interval): " + taskKey + " 间隔=" + intervalMinutes + "min");
            return true;
        } catch (Exception e) {
            System.err.println("[MyEnableScheduledService] 启动任务失败(interval): " + taskKey + " - " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
