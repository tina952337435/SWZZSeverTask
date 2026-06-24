package timertaskserver.workserver.service.TimerTask;

import org.springframework.beans.factory.annotation.Value;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TenMWSTask implements Runnable{

    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String nmcUrl = task.nmcUrl;
        List<String> urlList1 = new ArrayList<>();
        urlList1.add(nmcUrl + "publish/area/china/10mws_0000.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_0300.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_0600.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_0900.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_1200.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_1500.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_1800.html");
        urlList1.add(nmcUrl + "publish/area/china/10mws_2100.html");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        for(String url : urlList1){
            task.TenMWS(url,"全国10m风");
        }
        System.out.println(dateFormat.format(new Date()));
    }
}
