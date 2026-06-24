package timertaskserver.workserver.controller;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timertaskserver.tools.ResultUtils;
import timertaskserver.workserver.service.ClasspathDirectoryClassesFinder;
import timertaskserver.workserver.service.MyEnableScheduledService;

import java.util.List;

@RestController
@RequestMapping("/MyTimerTask")
public class MyTimerTaskController {
    @Autowired
    private MyEnableScheduledService service;


    @Value("${http.urlPath.defaultCron}")
    private String httpdefaultCron;

    @Value("${http.urlPath.intervalMinutes}")
    private long  httpintervalMinutes;

    @RequestMapping("/start/{className}")
    public ResultUtils<Object> startTimerTask(@PathVariable("className") String className, String cron){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String defaultCron = httpdefaultCron;//"0 */3 * * * ?";
        long intervalMinutes=httpintervalMinutes;
        if(null != cron){
            defaultCron = cron;
        }
        try {
            if(className.equals("WenDaiWBCTask")||className.equals("TaiFengWBCTask")||className.equals("ZGHYYBWaterTask")){
                defaultCron="0 */20 * * * ?";//20分钟请求一次
//                intervalMinutes=20;
            }
            else   if(className.equals("SynchronizeDataTask") ){//水位
                defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else if(className.equals("SynchronizeLLDataTask")){//流量
                defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else   if(className.equals("SynchronizeYLDataTask") ){//雨量
                defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else   if(className.equals("SynchronizeFXDataTask") ){//风向
                defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else if(className.equals("SynchronizeDataSWPT_SWTask")||className.equals("SynchronizeDataSWPT_LLTask")){
                defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else if(className.equals("SynchronizeGateDataTask") ){//水闸
                defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else if(className.equals("SynchronizeBengDataTask") ){//泵站
                defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }
            else if (className.equals("AutomaticFangjiangOverTask")) {
                defaultCron = "0 0/5 * * * ?";//每 n 分钟 执行一次
                intervalMinutes=5;
            }
            else   if(className.equals("SynchronizeQXYLDataTask") ){//气象雨量
                defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                intervalMinutes=3;
            }

            Class<?> taskClass = Class.forName("timertaskserver.workserver.service.TimerTask." + className);
            Object task = taskClass.newInstance();
//            boolean start = service.startTimerTask((Runnable) task, defaultCron);
            boolean start = service.startTimerTask((Runnable) task,intervalMinutes);
            if (start){
                return new ResultUtils<>(null,className+"启动成功",start);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
        }
        stopWatch.stop();
        return new ResultUtils<>(null,className+"启动失败",false);
    }

    @RequestMapping("/showDown/{className}")
    public ResultUtils<Object> showDownTimerTask(@PathVariable("className") String className){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean start = service.showDownTimerTask("timertaskserver.workserver.service.TimerTask." + className);
        if (start){
            return new ResultUtils<>(null,className+"关闭成功",start);
        }
        stopWatch.stop();
        return new ResultUtils<>(null,className+"关闭失败",false);
    }

    @RequestMapping("/restart/{className}")
    public ResultUtils<Object> restartTimerTask(@PathVariable("className") String className, String cron){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String defaultCron =httpdefaultCron;//"0 */3 * * * ?";
        long intervalMinutes=httpintervalMinutes;
        if(null != cron){
            defaultCron = cron;
        }
        if(className.equals("WenDaiWBCTask")||className.equals("TaiFengWBCTask")||className.equals("ZGHYYBWaterTask")){
            defaultCron="0 */20 * * * ?";//20分钟请求一次
            //intervalMinutes=20;
        }
        boolean start = service.restartTimerTask("timertaskserver.workserver.service.TimerTask." + className, defaultCron);

        if (start){
            return new ResultUtils<>(null,className+"重启成功",start);
        }
        stopWatch.stop();
        return new ResultUtils<>(null,className+"重启失败",false);
    }

    @RequestMapping("/startALL")
    public ResultUtils<Object> startTimerTaskALL(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String defaultCron =httpdefaultCron;// "0 */3 * * * ?";
        long intervalMinutes=httpintervalMinutes;
        StringBuilder stringBuilder = new StringBuilder();
        int num = 0;
        try {
            List<Class<?>> classList = ClasspathDirectoryClassesFinder.findClassesInClasspathDirectory("timertaskserver/workserver/service/TimerTask");
            for (Class className : classList){
                Class<?> taskClass = Class.forName(className.getName());
                Object task = taskClass.newInstance();
                if(className.getName().equals("WenDaiWBCTask")||className.getName().equals("TaiFengWBCTask")||className.getName().equals("ZGHYYBWaterTask")){
                    defaultCron="0 */20 * * * ?";//20分钟请求一次
                    //intervalMinutes=20;
                }
                else   if(className.equals("SynchronizeDataTask") ){//水位
                    defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
                else if(className.equals("SynchronizeLLDataTask")){//流量
                    defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
                else   if(className.equals("SynchronizeYLDataTask") ){//雨量
                    defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
                else   if(className.equals("SynchronizeFXDataTask") ){//风向
                    defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
                else if(className.equals("SynchronizeDataSWPT_SWTask")||className.equals("SynchronizeDataSWPT_LLTask")){
                    defaultCron = "0 0/3* * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
                else   if(className.equals("SynchronizeGateDataTask") ){//水闸
                    defaultCron = "0 0/3 * * * ?";//每 n 分钟 执行一次
                    intervalMinutes=3;
                }
//                boolean start = service.startTimerTask((Runnable) task, defaultCron);
                boolean start = service.startTimerTask((Runnable) task,intervalMinutes);
                if (start){
                    num++;
                }
                stringBuilder.append(className.getName().substring(className.getName().lastIndexOf(".")+1))
                .append(",");
            }
            if (num>0){
                return new ResultUtils<>(null,stringBuilder.toString()+"启动成功",true);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        return new ResultUtils<>(null,stringBuilder.toString()+"启动失败",false);
    }
}
