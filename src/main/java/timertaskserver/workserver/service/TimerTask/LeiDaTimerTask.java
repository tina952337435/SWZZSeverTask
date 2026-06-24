package timertaskserver.workserver.service.TimerTask;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 雷达
 */
public class LeiDaTimerTask implements Runnable{
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String nmcUrl = task.nmcUrl;
        String url = nmcUrl + "publish/radar/huadong.html";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        task.WrapTask(url,"华东雷达拼图","/RDCP/");
        System.out.println(dateFormat.format(new Date()));
    }
}
