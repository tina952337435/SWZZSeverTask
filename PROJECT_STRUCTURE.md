# TimerTaskServer 项目结构文档

## 项目概述

- **项目名称**: TimerTaskServer (上海水文定时任务服务器)
- **项目类型**: Spring Boot REST API 应用
- **技术栈**: Spring Boot 2.1.7 + MyBatis Plus + Maven
- **服务端口**: 1000

---

## 项目架构

```
SWZZSeverTask/
├── src/main/java/timertaskserver/
│   ├── TimerTaskApplication.java              # Spring Boot 启动类
│   │
│   ├── system/                                # 系统配置类
│   │   ├── DataSourceConfig.java              # 多数据源配置
│   │   ├── ScheduledConfig.java               # 线程池任务调度器配置
│   │   ├── FileConfig.java                    # 文件配置
│   │   ├── SwzzModeConfig.java                # 模式数据源配置
│   │   ├── SwzzQxsjConfig.java                # 气象水文数据源配置
│   │   ├── SwzzWaterConfig.java               # 水文水资源数据源配置
│   │   └── ZJ_TYPHOONDataConfig.java          # 台风数据源配置
│   │
│   ├── tools/                                 # 工具类
│   │   ├── MyTimerTask.java                   # 核心定时任务逻辑(40000+行)
│   │   ├── apihelper.java                     # API辅助类
│   │   ├── CommonUtills.java                   # 通用工具类
│   │   ├── DateUtil.java                       # 日期工具类
│   │   ├── JsonUtil.java                       # JSON工具类
│   │   ├── FilePathUtils.java                  # 文件路径工具类
│   │   ├── FtpFolderDownloader.java            # FTP文件夹下载器
│   │   ├── GridRainReader.java                 # 网格雨量读取
│   │   ├── convertGrb2ToNcUtil.java            # GRIB转NetCDF工具
│   │   ├── ShpToGeojson.java                   # Shapefile转GeoJSON
│   │   ├── TideDataInterpolator.java           # 潮位数据插值
│   │   ├── MyTimerTask.java                    # 定时任务主类
│   │   ├── ResultUtils.java                    # 响应结果封装
│   │   ├── ConditionVo.java                    # 查询条件对象
│   │   ├── EntityUtils.java                    # 实体工具类
│   │   └── PhotoUtils.java                     # 图片工具类
│   │
│   ├── workserver/
│   │   ├── controller/
│   │   │   └── MyTimerTaskController.java      # REST API控制器
│   │   │
│   │   ├── service/
│   │   │   ├── MyEnableScheduledService.java   # 任务调度服务
│   │   │   ├── ClasspathDirectoryClassesFinder.java  # 类路径扫描
│   │   │   ├── ApplicationContextUtil.java      # Spring上下文工具
│   │   │   ├── MyFtpClient.java                 # FTP客户端
│   │   │   ├── ThreadPoolTaskSchedulerPackage.java  # 任务包装类
│   │   │   └── TimerTask/                       # 定时任务实现类
│   │   │       ├── SynchronizeDataTask.java     # 水位数据同步
│   │   │       ├── SynchronizeLLDataTask.java   # 流量数据同步
│   │   │       ├── SynchronizeYLDataTask.java   # 雨量数据同步
│   │   │       ├── SynchronizeFXDataTask.java   # 风向数据同步
│   │   │       ├── SynchronizeGateDataTask.java # 水闸数据同步
│   │   │       ├── SynchronizeDataSWPT_SWTask.java  # 水文平台水位
│   │   │       ├── SynchronizeDataSWPT_LLTask.java  # 水文平台流量
│   │   │       ├── WenDaiWBCTask.java          # 温带风暴潮
│   │   │       ├── TaiFengWBCTask.java         # 台风风暴潮
│   │   │       ├── ZGHYYBWaterTask.java        # 中国海域预报
│   │   │       ├── TaiFengLJTask.java          # 台风路径
│   │   │       ├── FQWaterTask.java            # 分区水位预报
│   │   │       ├── FQ6HourWaterTask.java       # 6小时分区预报
│   │   │       ├── FQ336HourWaterTask.java     # 336小时预报
│   │   │       ├── XHWaterTask.java            # 巡航水位任务
│   │   │       ├── TFXSTask.java               # 台风巡视任务
│   │   │       ├── AutomaticCalculationTask.java      # 自动计算
│   │   │       ├── AutomaticCalculationTIDALTask.java # 潮位自动计算
│   │   │       ├── AutomaticCalculationSWIC.java       # 洪水预警计算
│   │   │       ├── LeiDaTimerTask.java         # 雷达任务
│   │   │       ├── QGJUTimerTask.java          # 全国降雨任务
│   │   │       ├── QGJU6TimerTask.java         # 6小时全国降雨
│   │   │       ├── TenMWSTask.java             # 10米风任务
│   │   │       ├── WindyTask.java              # 风场任务
│   │   │       ├── FYTwoHTask.java             # FY2H卫星任务
│   │   │       ├── GateHisDataTask.java        # 水闸历史数据
│   │   │       ├── removeAllQXFile.java        # 清理气象文件
│   │   │       └── removeAutoModeFangTask.java # 清理自动模式
│   │   │
│   │   ├── data/                              # 数据访问层
│   │   │   ├── swzzmode/                      # 模式数据
│   │   │   │   ├── SDE_AREAData.java
│   │   │   │   ├── SDE_AREA6HOURData.java
│   │   │   │   ├── DD_AUTOMATICData.java
│   │   │   │   └── DD_SOLUTIONData.java
│   │   │   │
│   │   │   ├── swzzqxsj/                      # 气象水文数据
│   │   │   │   ├── St_tide_rybData.java       # 潮位数据
│   │   │   │   ├── St_rnfl_fData.java         # 雨量数据
│   │   │   │   ├── St_windyweater_rData.java  # 风向风速数据
│   │   │   │   ├── Tba_weacontentData.java    # 气象内容数据
│   │   │   │   ├── Tz_gridData.java           # 网格数据
│   │   │   │   ├── Tz_ncfileData.java         # NC文件数据
│   │   │   │   ├── Tz_ncfilelistData.java     # NC文件列表
│   │   │   │   ├── Tz_watershedData.java      # 流域数据
│   │   │   │   ├── Tz_watersheddataData.java  # 流域实测数据
│   │   │   │   ├── Tz_watershedwgData.java    # 流域网格数据
│   │   │   │   ├── TzgriddataData.java        # 网格雨量数据
│   │   │   │   └── St_areatide_rybData.java   # 区域潮位
│   │   │   │
│   │   │   ├── swzzwater/                     # 水文水资源数据
│   │   │   │   ├── ST_STBPRP_BData.java       # 测站基本信息
│   │   │   │   ├── ST_STBPRP_B_STCDData.java  # 测站编码数据
│   │   │   │   ├── ST_GATE_RData.java         # 水闸数据
│   │   │   │   ├── ST_GATE_RNEWData.java      # 新版水闸数据
│   │   │   │   └── RTSQBZKDData.java          # 实时水位数据
│   │   │   │
│   │   │   └── zjtyphoon/                     # 台风数据
│   │   │       ├── ZJ_TFData.java             # 台风数据
│   │   │       ├── ZJ_TFLSLJData.java         # 台风历史路径
│   │   │       ├── ZJ_TFYBLJData.java          # 台风预报路径
│   │   │       └── ZJ_XSData.java             # 台风巡视数据
│   │   │
│   │   └── pojo/                              # 数据实体类
│   │       ├── swzzmode/                      # 模式数据实体
│   │       ├── swzzqxsj/                      # 气象水文实体
│   │       ├── swzzwater/                     # 水文水资源实体
│   │       └── zjtyphoon/                     # 台风数据实体
│   │
│   └── workserver/
│
├── src/main/resources/
│   ├── application.properties                  # 应用配置文件
│   └── mapper/                               # MyBatis XML映射文件
│       ├── work/
│       │   ├── swzzmode/                     # 模式数据Mapper
│       │   ├── swzzqxsj/                     # 气象水文Mapper
│       │   ├── swzzwater/                    # 水文水资源Mapper
│       │   └── zjtyphoon/                    # 台风数据Mapper
│
├── pom.xml                                    # Maven依赖配置
└── logs/                                     # 日志目录
```

---

## 数据源配置

| 数据源名称 | 数据库类型 | 用途 |
|-----------|-----------|------|
| swzzmode | DM (达梦) | 模式数据 |
| swzzdata | DM (达梦) | 实测数据 |
| swzzqxsj | DM (达梦) | 气象水文数据 |
| swzzwater | DM (达梦) | 水文水资源数据 |
| swzzflood | DM (达梦) | 洪潮数据 |
| zjtyphoon | DM (达梦) | 台风数据 |

---

## REST API 接口

| 端点 | 方法 | 功能 |
|------|------|------|
| `/MyTimerTask/start/{className}` | GET | 启动指定定时任务 |
| `/MyTimerTask/showDown/{className}` | GET | 停止指定定时任务 |
| `/MyTimerTask/restart/{className}` | GET | 重启指定定时任务 |
| `/MyTimerTask/startALL` | GET | 启动所有定时任务 |

### 任务调度间隔配置

| 任务类型 | 执行间隔 |
|----------|----------|
| WenDaiWBCTask | 20分钟 |
| TaiFengWBCTask | 20分钟 |
| ZGHYYBWaterTask | 20分钟 |
| SynchronizeDataTask (水位) | 3分钟 |
| SynchronizeLLDataTask (流量) | 3分钟 |
| SynchronizeYLDataTask (雨量) | 3分钟 |
| SynchronizeFXDataTask (风向) | 3分钟 |
| SynchronizeGateDataTask (水闸) | 3分钟 |

---

## 核心类说明

### MyTimerTask.java (核心业务类)

主要方法:
- `YBJYTask()` - 预报降雨任务，下载气象预报图片
- `WrapTask()` - 华东雷达拼图下载
- `TenMWS()` - 10米风场图片下载
- `WenDaiWBC()` - 温带风暴潮数据获取
- `TaiFengWBC()` - 台风风暴潮数据获取
- `SynchronizeData()` - 水位数据同步
- `SynchronizeLLData()` - 流量数据同步
- `SynchronizeYLData()` - 雨量数据同步
- `SynchronizeFXData()` - 风向数据同步
- `SynchronizeGateData()` - 水闸数据同步

### TimerTask/*.java (任务执行类)

实现 `Runnable` 接口，每个类对应一个定时任务：
```java
public class SynchronizeDataTask implements Runnable {
    @Override
    public void run() {
        MyTimerTask task = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        task.SynchronizeData();
    }
}
```

### MyEnableScheduledService.java (调度服务)

核心方法:
- `startTimerTask(Runnable, String cron)` - 按Cron表达式启动
- `startTimerTask(Runnable, long intervalMinutes)` - 按间隔分钟启动
- `showDownTimerTask(String className)` - 停止任务
- `restartTimerTask(String className, String cron)` - 重启任务

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.1.7.RELEASE | 核心框架 |
| MyBatis Plus | 3.3.2 | ORM框架 |
| dynamic-datasource | 3.0.0 | 多数据源 |
| DM JDBC Driver | 11.2.0.1 | 达梦数据库驱动 |
| OkHttp | 3.14.9 | HTTP客户端 |
| GeoTools | 19.2 | 空间数据处理 |
| NetCDF | 4.6.14 | 网格数据处理 |
| Swagger2 | 2.9.2 | API文档 |
| FastJSON | 1.2.80 | JSON处理 |
| EasyPOI | 4.1.2 | Excel/Word处理 |

---

## 开发注意事项

1. **数据库连接**: 项目使用国产达梦数据库，JDBC URL格式为 `jdbc:dm://host:port`
2. **多数据源**: 通过 `dynamic-datasource-spring-boot-starter` 管理多个数据源
3. **任务调度**: 使用 `ThreadPoolTaskScheduler`，默认线程池大小为3
4. **热部署**: 启用 Spring DevTools，修改Java文件后自动重启
5. **文件路径**: 配置项 `file.path.templatefilepath` 指定文件存储根目录
6. **FTP服务**: 配置项 `http.urlPath.FtpIP` 和 `http.urlPath.FtpPort` 指定FTP服务器

---

## 配置示例 (application.properties)

```properties
server.port=1000
spring.datasource.swzzmode.jdbc-url=jdbc:dm://172.16.196.114:55236
spring.datasource.swzzmode.username=SWZZ_MODE
spring.datasource.swzzmode.password=Swzz~CY3k

http.urlPath.defaultCron=0 0/5 * * * ?
http.urlPath.intervalMinutes=5
file.path.templatefilepath=/home/vdb/gaoqi/ModeUploadDoc/
```
