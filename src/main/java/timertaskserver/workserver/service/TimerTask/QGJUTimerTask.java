package timertaskserver.workserver.service.TimerTask;

import org.springframework.beans.factory.annotation.Value;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QGJUTimerTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String nmcUrl = task.nmcUrl;
        List<String> urlList1 = new ArrayList<>();
        urlList1.add(nmcUrl + "publish/precipitation/1-day.html");
        urlList1.add(nmcUrl + "publish/precipitation/2-day.html");
        urlList1.add(nmcUrl + "publish/precipitation/3-day.html");
        urlList1.add(nmcUrl + "publish/precipitation/day4.html");
        urlList1.add(nmcUrl + "publish/precipitation/day5.html");
        urlList1.add(nmcUrl + "publish/precipitation/day6.html");
        urlList1.add(nmcUrl + "publish/precipitation/day7.html");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        task.YBJYTask(urlList1,"全国雨量预报");
        System.out.println(dateFormat.format(new Date()));
    }
}
