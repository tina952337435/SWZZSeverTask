package timertaskserver.tools;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import timertaskserver.workserver.pojo.swzzqxsj.GridParam;
import timertaskserver.workserver.pojo.swzzqxsj.GridPoint;
import timertaskserver.workserver.pojo.swzzqxsj.SDE_AREA;

import java.math.BigDecimal;

/**
 * 网格降雨数据读取处理器
 */
public class GridRainReader {
//    @Value("${file.path.templatefilepath}")
//    private static String txtUrl; // 基础文件路径(相当于C#中的txturl.Text)

public static  float readFloat(DataInputStream dis) throws IOException {
        try{
            byte[] bytes = new byte[4];
            dis.readFully(bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置为小端字节序
            return buffer.getFloat();
        }
        catch (Exception ex){
            return 0;
        }
}
    /**
     * 读取并处理网格降雨预报数据
     * @param path 数据文件路径
     * @param TM 基准时间字符串(格式:yyyy-MM-dd HH:mm:ss)
     * @param FPDR 预报时长(默认6)
     * @param HOUR 小时数(默认1)
     */
    public static List<SDE_AREA> readGridRain36010New(String path, String TM, int FPDR, int HOUR,String txtUrl) {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        List<SDE_AREA> listArea =new ArrayList<>();
        FileInputStream fs = null;
        DataInputStream br = null;
        try {
            writeLogTxtStr("==========6小时读取gridData6.txt开始=================","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
            // 1. 读取网格参数配置文件
            String filePath = txtUrl + "gridData6.txt";
            String txtStr = new String(Files.readAllBytes(Paths.get(filePath)));
            // 使用Gson解析JSON配置
            Gson gson = new Gson();
            List<GridParam> lines = gson.fromJson(txtStr,
                    new TypeToken<List<GridParam>>(){}.getType());
            writeLogTxtStr("==========6小时读取gridData6.txt结束=================","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
            // 2. 打开二进制数据文件
            fs = new FileInputStream(path);
            br = new DataInputStream(fs);

            long filelen = br.available();//流长度
            writeLogTxtStr("==========6小时流长度================="+filelen,"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
            if(filelen==9437232) {
                // 3. 读取文件头信息
                float sLgtd = readFloat(br);      // 起始经度
                float sLgtdVal = readFloat(br);    // 经度间隔
                int xCount = (int) readFloat(br);
                ;  // 经度方向格点数

                float sLttd = readFloat(br);
                ;      // 起始纬度
                float sLttdVal = readFloat(br);    // 纬度间隔
                int yCount = (int) readFloat(br);  // 纬度方向格点数

                float StartTime = readFloat(br);   // 起始时次(分钟)
                float EndTime = readFloat(br);     // 终止时次(分钟)
                float TimeInterval = readFloat(br);  // 时间间隔(分钟)

                // 跳过预留字段
                readFloat(br);
                readFloat(br);
                readFloat(br);

                // 4. 计算时间维度
                int timeSteps = (int) ((EndTime - StartTime) / TimeInterval) + 1;
                writeLogTxtStr("==========6小时计算时间维度：" + timeSteps + ",yCount：" + yCount + ",xCount：" + xCount, "FQ6HourWaterGong" + formattedDateLog + ".txt", txtUrl);

                // 5. 初始化三维数据数组
                GridPoint[][][] gridData = new GridPoint[yCount][xCount][timeSteps];
                StringBuilder sb = new StringBuilder("["); // 用于构建JSON输出
                StringBuilder sbStr = new StringBuilder("网格号,经度,纬度,预报值(mm),预报时间\n");

                // 6. 解析基准时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date baseTime = sdf.parse(TM);

                writeLogTxtStr("==========6小时读取三维网格数据开始==========", "FQ6HourWaterGong" + formattedDateLog + ".txt", txtUrl);
                // 7. 读取三维网格数据
                for (int t = 0; t < timeSteps; t++) {
                    int gridIndex = 0;
                    // 计算当前预报时间
                    Date currentTime = calculateForecastTime(baseTime, StartTime, TimeInterval, t);

                    // 遍历所有网格点
                    for (int y = 0; y < yCount; y++) {
                        float lttd = sLttd + y * sLttdVal; // 计算当前纬度
                        for (int x = 0; x < xCount; x++) {
                            float lgtd = sLgtd + x * sLgtdVal; // 计算当前经度

                            // 读取并存储网格点数据
                            gridData[y][x][t] = new GridPoint();
                            gridData[y][x][t].lgtd = lgtd;
                            gridData[y][x][t].lttd = lttd;
                            gridData[y][x][t].Value = readFloat(br);
                            gridData[y][x][t].ForecastTime = currentTime;
                            gridData[y][x][t].t = t;

                            // 处理有效数据(值>0)
                            if (gridData[y][x][t].Value > 0) {
                                sb.append(buildGridPointJson(lgtd, lttd,
                                        gridData[y][x][t].Value, t, gridIndex));
                            }

                            // 记录首时次数据到CSV
                            if (t == 0) {
                                sbStr.append(buildCsvLine(gridIndex, gridData[y][x][t], sdf));
                            }

                            gridIndex++;
                        }
                    }
                }
                sb.append("]");

                writeLogTxtStr("==========6小时读取三维网格数据结束==========","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);

                // 8. 处理小时级数据
                listArea =  processHourlyData(gridData, lines, baseTime, xCount, yCount,
                        (int)EndTime, sdf, TM, FPDR, path,txtUrl);

                writeLogTxtStr("==========6小时处理小时级数据结束==========listArea的长度："+listArea.size(),"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
            }
            else{
                //删除文件，等会重新下载
                File file = new File(path);
                String msg="";
                if(file.delete()) {
                    msg=path+"文件删除成功";
                } else {
                    msg=path+"文件删除失败";
                }
                writeLogTxtStr("=========================="+msg,"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            writeLogTxtStr("==========6小时预报数据处理报错=========="+ex.getMessage()+"\n\n","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
        } finally {
            // 确保资源关闭
            try { if (br != null) br.close(); } catch (IOException e) { e.printStackTrace(); }
            try { if (fs != null) fs.close(); } catch (IOException e) { e.printStackTrace(); }
        }
        return listArea;
    }

    // 辅助方法：计算预报时间
    private static Date calculateForecastTime(Date baseTime, float startTime,
                                              float interval, int step) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseTime);
        cal.add(Calendar.MINUTE, (int)(startTime + step * interval));
        return cal.getTime();
    }

    // 辅助方法：构建网格点JSON字符串
    private static String buildGridPointJson(float lgtd, float lttd,
                                             float value, int t, int gridIndex) {
        return String.format("{\"lgtd\":%.6f,\"lttd\":%.6f,\"drp\":%.1f,\"tm\":%d,\"num\":%d},",
                lgtd, lttd, value, t, gridIndex);
    }

    // 辅助方法：构建CSV行
    private static String buildCsvLine(int gridIndex, GridPoint point, SimpleDateFormat sdf) {
        return String.format("%d,%.6f,%.6f,%.1f,%s\n",
                gridIndex, point.lgtd, point.lttd, point.Value,
                sdf.format(point.ForecastTime));
    }

    // 处理小时级数据
    private static List<SDE_AREA> processHourlyData(GridPoint[][][] gridData, List<GridParam> lines,
                                   Date baseTime, int xCount, int yCount, int endTime,
                                   SimpleDateFormat sdf, String TM, int FPDR, String path,String txtUrl) {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
       try{
           int hourlySteps = endTime / 60; // 转换为小时数
           GridPoint[][][] gridDataHour = new GridPoint[yCount][xCount][hourlySteps];
           int stepsPerHour = endTime / 60; // 每小时数据点数
           StringBuilder sbHour = new StringBuilder();
           List<SDE_AREA> listArea = new ArrayList<>();
           Gson gson = new Gson();

           writeLogTxtStr("==========6小时读取三维网格数据==========hourlySteps总小时数据为"+hourlySteps,"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
           writeLogTxtStr("==========6小时读取三维网格数据==========stepsPerHour每小时数据点数为"+stepsPerHour,"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
           // 按小时处理数据
           for (int h = 0; h < hourlySteps; h++) {
               int gridIndex = 0;
               Date currentTime = calculateHourlyTime(baseTime, h);
               int startIdx = h * stepsPerHour;
               List<GridParam> lineGrid = new ArrayList<>();



               // 计算每小时累计值
               for (int x = 0; x < xCount; x++) {
                   for (int y = 0; y < yCount; y++) {
                       float sum = calculateHourlySum(gridData, x, y, startIdx, stepsPerHour);

                       // 准备区域数据
                       listArea.add(createAreaDto(gridIndex, sum, h));

                       // 存储小时数据
                       if (sum > 0) {
                           gridDataHour[y][x][h] = createHourlyGridPoint(
                                   gridData, x, y, startIdx, sum, currentTime, h);

                           // 构建输出
                           sbHour.append(buildGridPointJson(
                                   gridDataHour[y][x][h].lgtd,
                                   gridDataHour[y][x][h].lttd,
                                   sum, h, gridIndex));

                           // 匹配网格参数
                           int finalGridIndex = gridIndex;
                           Optional<GridParam> match = lines.stream()
                                   .filter(p -> p.num == finalGridIndex)
                                   .findFirst();
                           if (match.isPresent()) {
                               GridParam param = match.get();
                               param.drp = sum;
                               lineGrid.add(param);
                           }
                       }
                       gridIndex++;
                   }
               }

               writeLogTxtStr("==========6小时读取三维网格数据==========小时数据h为"+h+"开始写入json数据","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);

               // 写入小时数据JSON文件
               writeHourlyJson(gson, lineGrid, baseTime, currentTime,txtUrl);

               writeLogTxtStr("==========6小时读取三维网格数据==========小时数据h为"+h+"成功写入json数据","FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
           }

           // 写入日志和数据库
           Files.write(Paths.get("gridDataHour.txt"), sbHour.toString().getBytes());
           String ncpath = path.replace(txtUrl, "");
           if (!listArea.isEmpty()) {
               //insertDataWater6HourNew(listArea, TM, hourlySteps);
               //insertData(null, ncpath, TM, FPDR);
           }
           return listArea;
       }catch (Exception ex){
           writeLogTxtStr("==========6小时读取三维网格数据报错，错误为=========="+ex.getMessage(),"FQ6HourWaterGong"+formattedDateLog+".txt",txtUrl);
           return new ArrayList<>();
       }
    }

    // 其他辅助方法和数据库操作方法...
    /**
     * 写入每小时网格数据到JSON文件
     * @param gson Gson实例用于JSON序列化
     * @param lineGrid 当前小时的网格数据列表
     * @param baseTime 基准时间
     * @param currentTime 当前预报时间
     * @throws IOException 当文件写入失败时抛出
     */
    private static void writeHourlyJson(Gson gson, List<GridParam> lineGrid,
                                 Date baseTime, Date currentTime,String txtUrl) throws IOException {
        // 1. 序列化为格式化的JSON字符串
        String json = gson.toJson(lineGrid);

        // 2. 构建文件路径
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePathJson = txtUrl + "6h/" +
                timeFormat.format(baseTime) + "(" +
                timeFormat.format(currentTime) + ").json";

        // 3. 确保目录存在
        File dir = new File(txtUrl + "6h");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 写入文件(自动覆盖已存在文件)
        Files.write(Paths.get(filePathJson), json.getBytes());

        // 5. 日志记录(可选)
        System.out.println("已写入小时数据文件: " + filePathJson);
    }

    /**
     * 计算每小时预报的时间点
     * @param baseTime 基准时间(预报起始时间)
     * @param hourOffset 小时偏移量(从0开始)
     * @return 计算后的预报时间
     */
    private static Date calculateHourlyTime(Date baseTime, int hourOffset) {
        // 创建Calendar实例并设置基准时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseTime);

        // 添加指定小时数
        calendar.add(Calendar.HOUR_OF_DAY, hourOffset);

        // 返回计算后的时间
        return calendar.getTime();
    }

    /**
     * 计算指定网格点在每小时内的累计降水量
     * @param gridData 三维网格数据数组
     * @param x 经度方向索引
     * @param y 纬度方向索引
     * @param startIdx 起始时间索引
     * @param stepsPerHour 每小时包含的时间步数
     * @return 累计降水量(mm)
     */
    private static float calculateHourlySum(GridPoint[][][] gridData, int x, int y,
                                     int startIdx, int stepsPerHour) {
        float sum = 0f;
        // 累加该小时内所有时间步的降水量
        for (int t = 0; t < stepsPerHour; t++) {
            sum += gridData[x][y][startIdx + t].Value;
        }
        return sum;
    }
    /**
     * 创建每小时汇总的网格点数据对象
     * @param gridData 原始三维网格数据
     * @param x 经度方向索引
     * @param y 纬度方向索引
     * @param startIdx 起始时间索引
     * @param sum 累计降水量
     * @param currentTime 当前预报时间
     * @param h 小时索引
     * @return 构造的GridPoint对象
     */
    private static GridPoint createHourlyGridPoint(GridPoint[][][] gridData, int x, int y,
                                            int startIdx, float sum,
                                            Date currentTime, int h) {
        GridPoint pojo=new GridPoint();
        pojo.setLgtd(gridData[x][y][startIdx].lgtd);// 经度(取第一个时间步的值)
        pojo.setLttd(gridData[x][y][startIdx].lttd);// 纬度(取第一个时间步的值)
        pojo.setValue(sum);
        pojo.setForecastTime(currentTime);
        pojo.setT(h);
        return pojo;
//        return new GridPoint(
//                gridData[x][y][startIdx].lgtd,
//                gridData[x][y][startIdx].lttd,
//                sum,                            // 累计降水量
//                currentTime,                    // 预报时间
//                h                               // 小时索引
//        );
    }

    private static SDE_AREA  createAreaDto(int gridindex,float sum,int h){
        SDE_AREA areaPojo=new SDE_AREA();
        areaPojo.setFID(gridindex);
        areaPojo.setZVALUE((float)sum);
        areaPojo.setCOLOR((float)h);
        return areaPojo;
    }

    public static void writeLogTxtStr(String logStr,String filename,String filePathName){
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            logStr = formattedDateLog+"："+logStr+"\n";
            byte[] bytes = logStr.getBytes(StandardCharsets.UTF_8);

            // 使用FileOutputStream构造方法的第二个参数true表示追加模式
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/"+filename, true);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

