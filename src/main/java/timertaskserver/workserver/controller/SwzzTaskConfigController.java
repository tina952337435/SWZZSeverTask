package timertaskserver.workserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import timertaskserver.tools.ResultUtils;
import timertaskserver.workserver.pojo.swzzdata.SwzzTaskConfigPojo;
import timertaskserver.workserver.service.TaskConfigService;

import java.util.List;

@RestController
@RequestMapping("/MyTimerTask/taskConfig")
public class SwzzTaskConfigController {

    @Autowired
    private TaskConfigService taskConfigService;

    /**
     * 获取所有任务配置
     */
    @GetMapping("/list")
    public ResultUtils<List<SwzzTaskConfigPojo>> listAll() {
        List<SwzzTaskConfigPojo> configs = taskConfigService.getAllConfigs();
        return new ResultUtils<>(configs, "查询成功", true);
    }

    /**
     * 获取启用的任务配置
     */
    @GetMapping("/enabled")
    public ResultUtils<List<SwzzTaskConfigPojo>> listEnabled() {
        List<SwzzTaskConfigPojo> configs = taskConfigService.getEnabledConfigs();
        return new ResultUtils<>(configs, "查询成功", true);
    }

    /**
     * 获取单个任务配置
     */
    @GetMapping("/{taskId}")
    public ResultUtils<SwzzTaskConfigPojo> getById(@PathVariable("taskId") String taskId) {
        SwzzTaskConfigPojo config = taskConfigService.getConfigById(taskId);
        if (config != null) {
            return new ResultUtils<>(config, "查询成功", true);
        }
        return new ResultUtils<>(null, "任务不存在", false);
    }

    /**
     * 启用任务
     */
    @GetMapping("/enable/{taskId}")
    public ResultUtils<Object> enable(@PathVariable("taskId") String taskId) {
        boolean success = taskConfigService.enableTask(taskId);
        if (success) {
            return new ResultUtils<>(null, "任务已启用", true);
        }
        return new ResultUtils<>(null, "任务启用失败", false);
    }

    /**
     * 禁用任务
     */
    @GetMapping("/disable/{taskId}")
    public ResultUtils<Object> disable(@PathVariable("taskId") String taskId) {
        boolean success = taskConfigService.disableTask(taskId);
        if (success) {
            return new ResultUtils<>(null, "任务已禁用", true);
        }
        return new ResultUtils<>(null, "任务禁用失败", false);
    }

    /**
     * 保存任务配置
     */
    @PostMapping("/save")
    public ResultUtils<Object> save(@RequestBody SwzzTaskConfigPojo config) {
        int result = taskConfigService.saveConfig(config);
        if (result >= 0) {
            return new ResultUtils<>(null, "保存成功", true);
        }
        return new ResultUtils<>(null, "保存失败", false);
    }

    /**
     * 批量启用所有任务
     */
    @GetMapping("/startAll")
    public ResultUtils<Object> startAll() {
        int count = taskConfigService.startAllEnabledTasks();
        return new ResultUtils<>(null, "成功启动 " + count + " 个任务", true);
    }

    /**
     * 获取运行状态
     */
    @GetMapping("/status")
    public ResultUtils<Object> getStatus() {
        int runningCount = taskConfigService.getRunningTaskCount();
        return new ResultUtils<>(runningCount, "当前运行任务数", true);
    }
}