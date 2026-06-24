package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.io.IOException;

public class SyncDataYJXYTask implements Runnable{
    @Override
    public void run() {
        try {
            MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
            task.SyncDataYJXY();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
