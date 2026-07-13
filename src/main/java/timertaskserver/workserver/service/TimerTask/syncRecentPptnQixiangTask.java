package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.io.IOException;

public class syncRecentPptnQixiangTask implements Runnable {
    @Override
    public void run() {
        try {
            MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
            task.syncRecentPptnQixiang();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
