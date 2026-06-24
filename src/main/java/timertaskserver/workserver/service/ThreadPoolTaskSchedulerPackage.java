package timertaskserver.workserver.service;

import lombok.Data;

import java.util.concurrent.ScheduledFuture;
@Data
public class ThreadPoolTaskSchedulerPackage {
    private Class<? extends Runnable> runnableClass;
    private ScheduledFuture<?> scheduledFuture;
    private String cron;

    private long interval;  // 新增字段

    // 新增方法
    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
