package timertaskserver.workserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import  java.util.concurrent.TimeUnit;

@Component
public class MyEnableScheduledService {
    @Autowired
    @Qualifier("MyThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private Map<String,ThreadPoolTaskSchedulerPackage> taskMap = new HashMap<>();

    public boolean startTimerTask(Runnable runTask,String cron){
        try {
            //String cron = "0 */5 * * * ?";
            CronTrigger cronTrigger = new CronTrigger(cron);
            ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(runTask, cronTrigger);
            ThreadPoolTaskSchedulerPackage taskSchedulerPackage = new ThreadPoolTaskSchedulerPackage();
            taskSchedulerPackage.setRunnableClass(runTask.getClass());
            taskSchedulerPackage.setScheduledFuture(scheduledFuture);
            taskSchedulerPackage.setCron(cron);
            taskMap.put(runTask.getClass().getName(),taskSchedulerPackage);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean showDownTimerTask(String className){
        if(taskMap.containsKey(className)){
            ThreadPoolTaskSchedulerPackage taskSchedulerPackage = taskMap.get(className);
            ScheduledFuture<?> scheduledFuture = taskSchedulerPackage.getScheduledFuture();
            if(scheduledFuture != null){
                return scheduledFuture.cancel(true);
            }
        }
        return false;
    }

    public boolean restartTimerTask(String className,String cron){
        if(taskMap.containsKey(className)){
            ThreadPoolTaskSchedulerPackage taskSchedulerPackage = taskMap.get(className);
            ScheduledFuture<?> scheduledFuture = taskSchedulerPackage.getScheduledFuture();
            if(scheduledFuture != null){
                scheduledFuture.cancel(true);
            }
            try {
                Runnable runnable = taskSchedulerPackage.getRunnableClass().newInstance();
                ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(runnable, new CronTrigger(cron));
                taskSchedulerPackage.setScheduledFuture(schedule);
                taskSchedulerPackage.setCron(cron);
                taskMap.put(runnable.getClass().getName(),taskSchedulerPackage);
                return true;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean startTimerTask(Runnable runTask, long intervalMinutes) {
            try {
                ThreadPoolTaskSchedulerPackage taskSchedulerPackage = new ThreadPoolTaskSchedulerPackage();
                // 将分钟转换为毫秒
                long intervalMillis = intervalMinutes * 60 * 1000;
                ScheduledFuture<?> schedule = threadPoolTaskScheduler.scheduleAtFixedRate(
                        runTask,
                        Instant.now().plusMillis(0),  // 立即执行
                        Duration.ofMillis(intervalMillis)
                );
                taskSchedulerPackage.setScheduledFuture(schedule);
                taskSchedulerPackage.setInterval(intervalMinutes);
                taskMap.put(runTask.getClass().getName(), taskSchedulerPackage);
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        return false;
    }
}
