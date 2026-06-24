package timertaskserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import timertaskserver.tools.javalog;
import timertaskserver.workserver.service.TaskConfigService;

@Component
public class AutoStartAllTaskRunner implements ApplicationRunner {

    @Autowired
    private TaskConfigService taskConfigService;

     @Value("${file.path.templatefilepath}")
    private String filePathName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("========================================");
        System.out.println("===== 定时任务自动启动中，请稍候... =====");
        System.out.println("========================================");

        new javalog().writelog("========================================", filePathName,"AutoTaskLog");
        new javalog().writelog("===== 定时任务自动启动中，请稍候... =====", filePathName,"AutoTaskLog");
        new javalog().writelog("========================================", filePathName,"AutoTaskLog");

        try {
            // 等待 Spring Boot 完全启动
            Thread.sleep(5000);

            // 根据数据库配置启动所有启用的任务
            int successNum = taskConfigService.startAllEnabledTasks();

            System.out.println("========================================");
            System.out.println("===== 定时任务自动启动完成 =====");
            System.out.println("===== 成功启动: " + successNum + " 个任务 =====");
            System.out.println("========================================");


            new javalog().writelog("========================================", filePathName,"AutoTaskLog");
            new javalog().writelog("===== 定时任务自动启动完成 =====", filePathName,"AutoTaskLog");
            new javalog().writelog("===== 成功启动: " + successNum + " 个任务 =====", filePathName,"AutoTaskLog");
            new javalog().writelog("========================================", filePathName,"AutoTaskLog");

        } catch (Exception e) {
            System.err.println("===== 定时任务自动启动异常 =====");
            new javalog().writelog("===== 定时任务自动启动异常 =====", filePathName,"AutoTaskLog");
            // e.printStackTrace();
        }
    }
}