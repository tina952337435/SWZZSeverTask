package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaiFengWBCTask implements Runnable{
    @Override
    public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        MyTimerTask timerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
//        timerTask.TaiFengWBC();
        timerTask.ZGHYYBWaterTask47("63405800,63405900,63401750,62701710","台风风暴潮");
        System.out.println(dateFormat.format(new Date()));
    }
}
