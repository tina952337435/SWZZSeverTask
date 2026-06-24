package timertaskserver.workserver.service.TimerTask;

import org.springframework.beans.factory.annotation.Value;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FYTwoHTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask timerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String nmcUrl= timerTask.nmcUrl;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        String url2 = nmcUrl + "publish/satellite/fy4b-visible.htm";
        timerTask.WrapTask(url2,"风云二号","/WXBL/");
        String url = nmcUrl + "publish/satellite/fy2c-disc-color.html";
        timerTask.WrapTask(url,"彩色圆盘图","/WXBL/");
        String url1 = nmcUrl + "publish/observations/china/dm/weatherchart-h000.htm";
        timerTask.WrapTask(url1,"地面天气图","/WESA/");
        System.out.println(dateFormat.format(new Date()));
    }
}
