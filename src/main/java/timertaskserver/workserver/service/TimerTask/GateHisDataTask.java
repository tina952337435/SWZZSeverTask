package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工情
 */
public class GateHisDataTask implements Runnable{

    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println((new Date()));
        MyTimerTask timerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        timerTask.GateHisDataTask();
        System.out.println(dateFormat.format(new Date()));
    }
}
