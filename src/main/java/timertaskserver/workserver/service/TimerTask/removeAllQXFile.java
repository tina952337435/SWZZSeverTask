package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.io.IOException;

public class removeAllQXFile implements Runnable{
    @Override
    public void run() {
        try {
            MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
            task.removeAllQXFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}