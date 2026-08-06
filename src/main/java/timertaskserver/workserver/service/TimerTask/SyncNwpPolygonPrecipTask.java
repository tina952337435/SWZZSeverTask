package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncNwpPolygonPrecipTask implements Runnable {
    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()) + " =========开始同步NWP面雨量预报数据=========");
        MyTimerTask timerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        timerTask.syncNwpPolygonPrecip();
        System.out.println(dateFormat.format(new Date()) + " =========同步NWP面雨量预报数据结束=========");
    }
}
