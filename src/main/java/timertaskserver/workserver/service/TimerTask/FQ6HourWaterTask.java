package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.io.IOException;

public class FQ6HourWaterTask implements Runnable{
    @Override
    public void run() {
        try {
            MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
//            task.FQ6HourWater();
//            task.FQ6HourWaterGong();
               task.FQ6HourWaterGong6();
               //task.FQ6HourWaterGong6Test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
