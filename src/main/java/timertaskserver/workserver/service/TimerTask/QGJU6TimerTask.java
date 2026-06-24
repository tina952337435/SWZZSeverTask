package timertaskserver.workserver.service.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QGJU6TimerTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String nmcUrl = task.nmcUrl;
        List<String> urlList2 = new ArrayList<>();
        urlList2.add(nmcUrl + "publish/precipitation/6hours-6.html");
        urlList2.add(nmcUrl + "publish/precipitation/6hours-12.html");
        urlList2.add(nmcUrl + "publish/precipitation/6hours-18.html");
        urlList2.add(nmcUrl + "publish/precipitation/6hours-24.html");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        task.YBJYTask(urlList2,"6小时雨量预报");
        System.out.println(dateFormat.format(new Date()));
    }
}
