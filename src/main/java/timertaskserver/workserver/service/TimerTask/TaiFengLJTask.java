package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaiFengLJTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        int year = LocalDateTime.now().getYear();
        task.TaiFengLJTask(String.valueOf(year));
    }
}
