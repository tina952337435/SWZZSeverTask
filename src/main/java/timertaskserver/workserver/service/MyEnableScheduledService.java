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
            String taskKey = runTask.getClass().getName();
            // 先取消已存在的旧任务，防止重复调度
            ThreadPoolTaskSchedulerPackage existing = taskMap.get(taskKey);
            if (existing != null && existing.getScheduledFuture() != null) {
                existing.getScheduledFuture().cancel(true);
                System.out.println("[MyEnableScheduledService] 取消旧任务(cron): " + taskKey);
            }
            Runnable wrappedTask = new MutexTaskWrapper(runTask);
            CronTrigger cronTrigger = new CronTrigger(cron);
            ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(wrappedTask, cronTrigger);
            ThreadPoolTaskSchedulerPackage taskSchedulerPackage = new ThreadPoolTaskSchedulerPackage();
            taskSchedulerPackage.setRunnableClass(runTask.getClass());
            taskSchedulerPackage.setScheduledFuture(scheduledFuture);
            taskSchedulerPackage.setCron(cron);
            taskMap.put(taskKey, taskSchedulerPackage);
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
                Runnable wrappedTask = new MutexTaskWrapper(runnable);
                ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(wrappedTask, new CronTrigger(cron));
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
                String taskKey = runTask.getClass().getName();
                // 先取消已存在的旧任务，防止重复调度
                ThreadPoolTaskSchedulerPackage existing = taskMap.get(taskKey);
                if (existing != null && existing.getScheduledFuture() != null) {
                    existing.getScheduledFuture().cancel(true);
                    System.out.println("[MyEnableScheduledService] 取消旧任务(interval): " + taskKey);
                }
                Runnable wrappedTask = new MutexTaskWrapper(runTask);
                ThreadPoolTaskSchedulerPackage taskSchedulerPackage = new ThreadPoolTaskSchedulerPackage();
                // 将分钟转换为毫秒
                long intervalMillis = intervalMinutes * 60 * 1000;
                ScheduledFuture<?> schedule = threadPoolTaskScheduler.scheduleAtFixedRate(
                        wrappedTask,
                        Instant.now().plusMillis(0),  // 立即执行
                        Duration.ofMillis(intervalMillis)
                );
                taskSchedulerPackage.setScheduledFuture(schedule);
                taskSchedulerPackage.setInterval(intervalMinutes);
                taskMap.put(taskKey, taskSchedulerPackage);
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        return false;
    }
}
