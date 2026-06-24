package timertaskserver.tools;


import timertaskserver.workserver.pojo.swzzqxsj.St_tide_rybPojo;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TideDataInterpolator {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<St_tide_rybPojo> interpolateHourlyTo5Min(List<St_tide_rybPojo> hourlyData)  {
        if (hourlyData == null || hourlyData.size() < 2) {
            return hourlyData;
        }

        List<St_tide_rybPojo> result = new ArrayList<>();
        try {
            hourlyData = hourlyData.stream()
                    .sorted(Comparator.comparing(St_tide_rybPojo::getTM))
                    .collect(Collectors.toList());

            for (int i = 0; i < hourlyData.size() - 1; i++) {
                St_tide_rybPojo current = hourlyData.get(i);
                St_tide_rybPojo next = hourlyData.get(i + 1);

                // 添加原始数据点
                result.add(current);

                // 解析时间字符串为Date对象
                Date currentTime = dateFormat.parse(current.getTM());
                Date nextTime = dateFormat.parse(next.getTM());

                // 计算时间间隔(毫秒)
                long durationMillis = nextTime.getTime() - currentTime.getTime();
                double totalHours = durationMillis / (1000.0 * 60 * 60);

                // 每5分钟插值(共11个点:0,5,10,...,55分钟)
                for (int j = 1; j < 12; j++) {
                    double t = j * 5.0 / 60.0; // 转换为小时单位的比例
                    double ratio = t / totalHours;

                    St_tide_rybPojo interpolated = new St_tide_rybPojo();
                    interpolated.setSTCD(current.getSTCD());

                    // 计算插值时间
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentTime);
                    cal.add(Calendar.MINUTE, j * 5);
                    interpolated.setTM(dateFormat.format(cal.getTime()));

                    interpolated.setYBTM(current.getYBTM());
                    interpolated.setTDZ(lerp(current.getTDZ(), next.getTDZ(), ratio));
                    interpolated.setRTYPE(current.getRTYPE());
                    interpolated.setNOTE("5分钟插值数据");
                    interpolated.setREMARK(String.format("插值于%s和%s之间",
                            current.getTM(), next.getTM()));

                    result.add(interpolated);
                }
            }

            // 添加最后一个原始数据点
            result.add(hourlyData.get(hourlyData.size() - 1));
        }catch (ParseException ex){}
        return result.stream()
                .sorted(Comparator.comparing(St_tide_rybPojo::getTM))
                .collect(Collectors.toList());
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
}

