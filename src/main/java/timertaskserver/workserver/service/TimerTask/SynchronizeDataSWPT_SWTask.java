package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.io.IOException;

public class SynchronizeDataSWPT_SWTask implements Runnable{
    @Override
    public void run() {
        try {
            MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
            task.SynchronizeDataSWPT_SW();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
