package timertaskserver.workserver.service.TimerTask;

import org.springframework.beans.factory.annotation.Value;
import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class WenDaiWBCTask implements Runnable{

    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
//        String nmefcUrl = task.nmefcUrl;
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateFormat.format(new Date()));
//        String[] paramArr = {"site"};
//        String[] paramValArr = {"%E9%AB%98%E6%A1%A5"};
//        List<String> param = Arrays.asList(paramArr);
//        List<String> paramVal = Arrays.asList(paramValArr);
//        task.WenDaiWBC(nmefcUrl + "api/data/temperate/statistics","10001010","温带",param,paramVal);
//        System.out.println(dateFormat.format(new Date()));//"https://www.nmefc.cn/api/data/temperate/statistics?site=%E9%AB%98%E6%A1%A5"

        task.ZGHYYBWaterTask47("10001010","温带风暴潮");
    }//
}
