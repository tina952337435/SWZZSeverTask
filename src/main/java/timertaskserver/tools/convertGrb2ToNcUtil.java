package timertaskserver.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class convertGrb2ToNcUtil<T>{
    public static void convertGrb2ToNc(String inputGrb2File, String outputNcFile) throws IOException, InterruptedException {
        // CDO命令格式：cdo copy input.grb2 output.nc
        String cdoCommand = "cdo copy";
        String command = cdoCommand + " " + inputGrb2File + " " + outputNcFile;

        // 执行命令
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash", "-c", command);
        Process process = builder.start();

        // 读取命令行输出信息（如果有必要的话）
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待命令执行完成
        process.waitFor();
        reader.close();
    }
}
