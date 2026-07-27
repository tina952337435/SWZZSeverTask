package timertaskserver.workserver.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 给所有定时任务统一加互斥锁，防止同一个任务并发执行。
 * 在调度层包装即可，无需修改每个任务类。
 */
public class MutexTaskWrapper implements Runnable {

    /** 每个任务类型一个锁，key 为任务类名 */
    private static final ConcurrentHashMap<String, AtomicBoolean> LOCKS = new ConcurrentHashMap<>();

    private final Runnable delegate;
    private final String taskKey;

    public MutexTaskWrapper(Runnable delegate) {
        this.delegate = delegate;
        this.taskKey = delegate.getClass().getName();
    }

    @Override
    public void run() {
        // 如果当前线程已被中断（被 Future.cancel(true) 取消），直接退出
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(LocalDateTime.now() + " [" + taskKey + "] 任务已被取消，跳过执行");
            return;
        }
        AtomicBoolean lock = LOCKS.computeIfAbsent(taskKey, k -> new AtomicBoolean(false));
        if (!lock.compareAndSet(false, true)) {
            System.out.println(LocalDateTime.now() + " [" + taskKey + "] 上一轮任务仍在执行，跳过本次");
            return;
        }
        try {
            System.out.println(LocalDateTime.now() + " [" + taskKey + "] 开始执行, 线程: "
                    + Thread.currentThread().getName());
            delegate.run();
            System.out.println(LocalDateTime.now() + " [" + taskKey + "] 执行完成, 线程: "
                    + Thread.currentThread().getName());
        } finally {
            lock.set(false);
        }
    }
}
