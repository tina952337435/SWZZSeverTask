package timertaskserver.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduledConfig {

    // @Bean(name = "MyThreadPoolTaskScheduler")
    // public ThreadPoolTaskScheduler MyThreadPoolTaskScheduler(){
    //     ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    //     threadPoolTaskScheduler.setPoolSize(20);
    //     threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
    //     return threadPoolTaskScheduler;
    // }

    @Bean(name = "MyThreadPoolTaskScheduler")
    public ThreadPoolTaskScheduler myThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // 1. 线程池大小：根据业务并发量调整，20通常足够
        scheduler.setPoolSize(20);
        
        // 2. 线程名称前缀：便于排查问题时定位线程
        scheduler.setThreadNamePrefix("my-scheduler-");
        
        // 3. 移除取消策略：设为true，确保取消的任务立即从队列移除，防止内存泄漏
        scheduler.setRemoveOnCancelPolicy(true);
        
        // 4. 【关键】关闭时等待任务完成：防止重启/停止时任务被强制中断导致数据不一致
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // 5. 【关键】等待终止时长：设置最大等待时间（秒），超过后强制销毁，防止无限等待导致阻塞
        scheduler.setAwaitTerminationSeconds(60);
        
        // 6. 【建议】错误处理器：防止单个任务异常导致整个调度器停止工作
        scheduler.setErrorHandler(t -> {
            // 记录日志，避免异常吞没
            System.err.println("定时任务执行异常: " + t.getMessage());
            t.printStackTrace();
        });

        return scheduler;
    }

}
