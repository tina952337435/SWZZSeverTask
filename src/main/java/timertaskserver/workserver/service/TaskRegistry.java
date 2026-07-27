package timertaskserver.workserver.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 所有定时任务的统一注册中心。
 * TaskConfigService 和 MyEnableScheduledService 共享此注册表，
 * 确保同一个任务类全局只有一个 ScheduledFuture。
 */
public class TaskRegistry {

    /** taskKey (类全名) -> ScheduledFuture */
    private static final Map<String, ScheduledFuture<?>> FUTURES = new ConcurrentHashMap<>();

    /**
     * 注册新任务。如果同 key 已有在运行的任务，先 cancel 旧的再注册新的。
     * @return true 表示注册成功
     */
    public static boolean register(String taskKey, ScheduledFuture<?> newFuture) {
        // 原子地替换：cancel 旧 Future，put 新 Future
        ScheduledFuture<?> old = FUTURES.put(taskKey, newFuture);
        if (old != null && !old.isCancelled()) {
            old.cancel(true);
            System.out.println("[TaskRegistry] 取消旧任务: " + taskKey);
        }
        return true;
    }

    /**
     * 注销任务（停止并从注册表移除）。
     */
    public static boolean unregister(String taskKey) {
        ScheduledFuture<?> future = FUTURES.remove(taskKey);
        if (future != null && !future.isCancelled()) {
            return future.cancel(true);
        }
        return false;
    }

    /**
     * 判断任务是否在运行中。
     */
    public static boolean isRunning(String taskKey) {
        ScheduledFuture<?> future = FUTURES.get(taskKey);
        return future != null && !future.isCancelled() && !future.isDone();
    }

    /**
     * 获取当前注册的任务数量。
     */
    public static int size() {
        return FUTURES.size();
    }
}
