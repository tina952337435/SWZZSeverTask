package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

public class TFXSTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        task.GetTFXSTask();
    }
}
