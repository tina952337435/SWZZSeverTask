package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WindyTask implements Runnable{
    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        MyTimerTask timerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        timerTask.IncreaseWindyTask();
        System.out.println(dateFormat.format(new Date()));
    }
}
