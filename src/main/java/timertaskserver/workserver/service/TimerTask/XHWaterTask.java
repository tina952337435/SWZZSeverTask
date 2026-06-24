package timertaskserver.workserver.service.TimerTask;

import timertaskserver.tools.MyTimerTask;
import timertaskserver.workserver.service.ApplicationContextUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class XHWaterTask implements Runnable{
    @Override
    public void run() {
        List<Map<String,String>> mapList = new ArrayList<>();
        Map<String,String> map1 = new HashMap<>();
        map1.put("STCD","10001001");
        map1.put("PATH","xujiahui");
        map1.put("TYPE","徐汇");
        mapList.add(map1);
        Map<String,String> map2 = new HashMap<>();
        map2.put("STCD","10310113");
        map2.put("PATH","baoshan");
        map2.put("TYPE","宝山");
        mapList.add(map2);
        Map<String,String> map3 = new HashMap<>();
        map3.put("STCD","10310120");
        map3.put("PATH","fengxian");
        map3.put("TYPE","奉贤");
        mapList.add(map3);
        Map<String,String> map4 = new HashMap<>();
        map4.put("STCD","10310114");
        map4.put("PATH","jiading");
        map4.put("TYPE","嘉定");
        mapList.add(map4);
        Map<String,String> map5 = new HashMap<>();
        map5.put("STCD","10310116");
        map5.put("PATH","jinshan");
        map5.put("TYPE","金山");
        mapList.add(map5);
        Map<String,String> map7 = new HashMap<>();
        map7.put("STCD","10310115");
        map7.put("PATH","pudong");
        map7.put("TYPE","浦东");
        mapList.add(map7);
        Map<String,String> map8 = new HashMap<>();
        map8.put("STCD","10310118");
        map8.put("PATH","qingpu");
        map8.put("TYPE","青浦");
        mapList.add(map8);
        Map<String,String> map9 = new HashMap<>();
        map9.put("STCD","10310117");
        map9.put("PATH","songjiang");
        map9.put("TYPE","松江");
        mapList.add(map9);
        Map<String,String> map10 = new HashMap<>();
        map10.put("STCD","10310112");
        map10.put("PATH","zuoxing");
        map10.put("TYPE","闵行");
        mapList.add(map10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        task.XHWater(mapList);
        System.out.println(dateFormat.format(new Date()));
    }
}
