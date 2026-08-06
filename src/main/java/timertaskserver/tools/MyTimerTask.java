package timertaskserver.tools;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import okhttp3.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import timertaskserver.workserver.data.swzzmode.DD_AUTOMATICData;
import timertaskserver.workserver.data.swzzmode.DD_SOLUTIONData;
import timertaskserver.workserver.data.swzzmode.SDE_AREA6HOURData;
import timertaskserver.workserver.data.swzzmode.SDE_AREAData;
import timertaskserver.workserver.data.swzzmode.StRnflRlistData;
import timertaskserver.workserver.data.swzzmode.StRnflRmodeData;
import timertaskserver.workserver.pojo.swzzmode.StRnflRlistPojo;
import timertaskserver.workserver.pojo.swzzmode.StRnflRmodePojo;
import timertaskserver.workserver.data.swzzqxsj.*;
import timertaskserver.workserver.data.swzzwater.*;
import timertaskserver.workserver.data.zjtyphoon.ZJ_TFData;
import timertaskserver.workserver.data.zjtyphoon.ZJ_TFLSLJData;
import timertaskserver.workserver.data.zjtyphoon.ZJ_TFYBLJData;
import timertaskserver.workserver.data.zjtyphoon.ZJ_XSData;
import timertaskserver.workserver.pojo.swzzmode.DD_AUTOMATICPojo;
import timertaskserver.workserver.pojo.swzzmode.DD_SOLUTIONPojo;
import timertaskserver.workserver.pojo.swzzmode.SDE_AREA6HOURPojo;
import timertaskserver.workserver.pojo.swzzmode.SDE_AREAPojo;
import timertaskserver.workserver.pojo.swzzqxsj.*;
import timertaskserver.workserver.pojo.swzzwater.*;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFLSLJPojo;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFPojo;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_TFYBLJPojo;
import timertaskserver.workserver.pojo.zjtyphoon.ZJ_XSPojo;
import timertaskserver.workserver.service.ApplicationContextUtil;
import timertaskserver.workserver.service.MyFtpClient;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.nio.ByteOrder;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

//定时器任务类
@Component
public class MyTimerTask {
    @Autowired
    private Tba_weacontentData data;
    @Autowired
    private St_tide_rybData rybData;
    @Autowired
    private ST_STBPRP_BData stbprpBData;
    @Autowired
    private St_windyweater_rData windData;
    @Autowired
    private St_rnfl_fData rnflFData;
    @Autowired
    private RTSQBZKDData rtsqbzkdData;
    @Autowired
    private ST_STBPRP_B_STCDData stcdData;
    @Autowired
    private ST_GATE_RNEWData gateRnewData;
    @Autowired
    private ST_GATE_RData gateRData;
    @Autowired
    private SDE_AREAData sdeAreaData;

    @Autowired
    private Tz_watershedwgData tz_watershedwgData;

    @Autowired
    private SDE_AREA6HOURData sdeArea6HOURData;
    @Autowired
    private Tz_watershedData tzWatershedData;
    @Autowired
    private Tz_watersheddataData tzWatersheddataData;
    @Autowired
    private Tz_ncfileData ncfileData;
    @Autowired
    private ZJ_TFData zj_tfData;
    @Autowired
    private ZJ_TFLSLJData zj_tflsljData;
    @Autowired
    private ZJ_TFYBLJData zj_tfybljData;
    @Autowired
    private Tz_ncfilelistData ncfilelistData;
    @Autowired
    private ZJ_XSData zj_xsData;

    @Autowired
    private DD_AUTOMATICData dd_automaticData;

    @Autowired
    private DD_SOLUTIONData dd_solutionData;

    @Autowired
    private StRnflRlistData stRnflRlistData;

    @Autowired
    private StRnflRmodeData stRnflRmodeData;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("swzzqxsjserveSessionFactory")
    private org.apache.ibatis.session.SqlSessionFactory swzzqxsjSqlSessionFactory;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("swzzmodeserveSessionFactory")
    private org.apache.ibatis.session.SqlSessionFactory swzzmodeSqlSessionFactory;

    @Value("${file.path.templatefilepath}")
    private String filePathName;
    @Value("${http.urlPath.imgUrl}")
    public String imgUrl;
    @Value("${http.urlPath.nmcUrl}")
    public String nmcUrl;
    @Value("${http.urlPath.weatherUrl}")
    public String weatherUrl;
    @Value("${http.urlPath.oceanguideUrl}")
    public String oceanguideUrl;
    @Value("${http.urlPath.nodewindyUrl}")
    public String nodewindyUrl;
    @Value("${http.urlPath.windyUrl}")
    public String windyUrl;
    @Value("${http.urlPath.typhoonUrl}")
    public String typhoonUrl;
    @Value("${http.urlPath.nmefcUrl}")
    public String nmefcUrl;

    @Value("${http.urlPath.ClientIP}")
    public String ClientIP;

    @Value("${http.urlPath.FtpIP}")
    public String FtpIP;

    @Value("${http.urlPath.FtpPort}")
    public Integer FtpPort;

    @Value("${http.urlPath.NewFtpIP}")
    public String NewFtpIP;

    @Value("${http.urlPath.NewFtpPort}")
    public Integer NewFtpPort;

    @Value("${http.urlPath.NewFtpUser}")
    public String NewFtpUser;

    @Value("${http.urlPath.NewFtpPass}")
    public String NewFtpPass;

    @Value("${http.urlPath.NewFtpDir48}")
    public String NewFtpDir48;

    @Value("${http.urlPath.NewFtpDir336}")
    public String NewFtpDir336;

    @Value("${http.urlPath.wgrib2Path}")
    public String wgrib2Path;

    @Value("${http.urlPath.ServerIP}")
    private String ServerIP;

    @Value("${http.urlPath.ServerIP47}")
    private String ServerIP47;

    @Value("${http.urlPath.waterlogging_api}")
    public String waterlogging_api;

    public static String getHtmlResourceByUrl(String url, String encoding) {
        StringBuffer buffer = new StringBuffer();
        URL urlObj = null;
        URLConnection urlConnection = null;
        InputStreamReader in = null;
        BufferedReader reader = null;

        try {
            urlObj = new URL(url);
            urlConnection = urlObj.openConnection();
            in = new InputStreamReader(urlConnection.getInputStream(), encoding);
            reader = new BufferedReader(in);
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    public static boolean downImage(String filePath, String imageUrl, String type) {
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (imageName.contains("?")) {
            imageName = imageName.substring(0, imageName.indexOf("?"));
        }
        if (imageName.equals("nodata.jpg")) {
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            MyTimerTask myTimerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
            URL urlObj = new URL(imageUrl.replace("http://image.nmc.cn/", myTimerTask.imgUrl));
            URLConnection urlConnection = urlObj.openConnection();
            InputStream in = urlConnection.getInputStream();
            String imagePath = "";
            switch (type) {
                case "华东雷达拼图":
                    imagePath = filePath + "//HDLD_" + imageName.substring(imageName.lastIndexOf("_") + 1);
                    break;
                case "预报降雨":
                    imagePath = filePath + "//" + imageName.substring(imageName.lastIndexOf("_") + 1);
                    break;
                case "10m风":
                    imagePath = filePath + "//NWPR_" + imageName.substring(imageName.lastIndexOf("_") + 1);
                    break;
            }
            FileOutputStream out = new FileOutputStream(new File(imagePath));
            int i = 0;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getImageName(String imageUrl, String type) {
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (imageName.contains("?")) {
            imageName = imageName.substring(0, imageName.indexOf("?"));
        }
        if (imageName.equals("nodata.jpg")) {
            return null;
        }
        switch (type) {
            case "华东雷达拼图":
                imageName = "HDLD_" + imageName.substring(imageName.lastIndexOf("_") + 1);
                break;
            case "预报降雨":
                imageName = imageName.substring(imageName.lastIndexOf("_") + 1);
                break;
            case "10m风":
                imageName = "NWPR_" + imageName.substring(imageName.lastIndexOf("_") + 1);
                break;
        }
        return imageName;
    }

    public void YBJYTask(List<String> urlList, String imgType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stime = dateFormat.format(new Date()) + " 00:00:00";
        String etime = dateFormat.format(new Date()) + " 23:59:59";
        List<Tba_weacontentPojo> tbaList = new ArrayList<>();
        System.out.println("=========开始下载" + imgType + "图片===========");
        for (String url : urlList) {
            Tba_weacontentPojo tba = new Tba_weacontentPojo();
            String html = MyTimerTask.getHtmlResourceByUrl(url, "UTF-8");
            Document document = Jsoup.parse(html);
            Element element = document.getElementById("imgpath");
            String imgUrl = element.attr("src");
            String hourStr = element.attr("data-fffmm");
            String date = imgUrl.substring("http://image.nmc.cn/product/".length(), imgUrl.indexOf("/STFC/"))
                    .replace("/", "-");
            String time = date + " 08:00:00";
            System.out.println(imgType + " : " + time + " : " + imgUrl);
            String imageName = getImageName(imgUrl, "预报降雨");
            List<Tba_weacontentPojo> pojos = data.selectList(null, imageName, stime, etime, null, null, null);
            if (null == imageName || pojos.size() > 0)
                continue;
            boolean isSuccess = MyTimerTask.downImage(filePathName + "qx//" + date.replace("-", ""), imgUrl, "预报降雨");
            tba.setTBA_WEAID(UUID.randomUUID().toString().replaceAll(" ", "-"));
            tba.setTBA_FILENAME(imageName);
            tba.setTBA_INFOTYPE(imgType);
            tba.setTBA_FILETYPE(imageName.substring(imageName.indexOf(".")));
            tba.setTBA_DESDATE(time);
            tba.setTBA_GETDATE(time);
            tba.setTBA_NOTE(String.valueOf(Integer.parseInt(hourStr)));
            if (isSuccess)
                tbaList.add(tba);
        }
        System.out.println("=========下载" + imgType + "图片结束===========");
        if (tbaList.size() > 0) {
            data.insertALL(tbaList);
        }
    }

    public void WrapTask(String url, String imgType, String suffix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String stime = dateFormat.format(new Date()) + " 00:00:00";
        String etime = dateFormat.format(new Date()) + " 23:59:59";
        List<Tba_weacontentPojo> tbaList = new ArrayList<>();
        String html = MyTimerTask.getHtmlResourceByUrl(url, "UTF-8");
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("timeWrap");
        List<Node> nodeList = element.childNodes();
        System.out.println("=========开始下载" + imgType + "图片===========");
        for (Node node : nodeList) {
            Tba_weacontentPojo tba = new Tba_weacontentPojo();
            String imgUrl = node.attr("data-img");
            String dataTime = node.attr("data-time");
            String date = imgUrl.substring("http://image.nmc.cn/product/".length(), imgUrl.indexOf(suffix)).replace("/",
                    "-");
            String time = dataTime.split(" ")[1];
            String tm = date + " " + time + ":00";
            System.out.println(imgType + " : " + tm + " : " + imgUrl);
            String imageName = getImageName(imgUrl, "华东雷达拼图");
            List<Tba_weacontentPojo> pojos = data.selectList(null, imageName, stime, etime, null, null, null);
            if (null == imageName || pojos.size() > 0)
                continue;
            boolean isSuccess = MyTimerTask.downImage(filePathName + "qx//" + date.replace("-", ""), imgUrl, "华东雷达拼图");
            tba.setTBA_WEAID(UUID.randomUUID().toString().replaceAll(" ", "-"));
            tba.setTBA_FILENAME(imageName);
            tba.setTBA_INFOTYPE(imgType);
            tba.setTBA_FILETYPE(imageName.substring(imageName.indexOf(".")));
            tba.setTBA_DESDATE(tm);
            tba.setTBA_GETDATE(tm);
            // tba.setTBA_NOTE(String.valueOf(Integer.parseInt(hourStr)));
            if (isSuccess)
                tbaList.add(tba);
        }
        System.out.println("=========下载" + imgType + "图片结束===========");
        if (tbaList.size() > 0) {
            data.insertALL(tbaList);
        }
    }

    public void TenMWS(String url, String imgType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stime = dateFormat.format(new Date()) + " 00:00:00";
        String etime = dateFormat.format(new Date()) + " 23:59:59";
        List<Tba_weacontentPojo> tbaList = new ArrayList<>();
        String html = MyTimerTask.getHtmlResourceByUrl(url, "UTF-8");
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("fffmmWrap");
        List<Node> nodeList = element.childNodes();
        System.out.println("=========开始下载" + imgType + "图片===========");
        for (Node node : nodeList) {
            Tba_weacontentPojo tba = new Tba_weacontentPojo();
            String imgUrl = node.attr("data-img");
            String dataTime = node.attr("data-time");
            String dataIndex = node.attr("data-index");
            String[] strings = dataTime.split(" ");
            String tm = strings[0].substring(0, 4) + "-" + strings[0].substring(4, 6) + "-" + strings[0].substring(6)
                    + " " + strings[1] + ":00";
            String hourStr = "00";
            if (!"0".equals(dataIndex)) {
                hourStr = strings[2].substring(0, 2);
            }
            System.out.println(imgType + " : " + tm + " " + hourStr + "小时 : " + imgUrl);
            String imageName = getImageName(imgUrl, "10m风");
            List<Tba_weacontentPojo> pojos = data.selectList(null, imageName, stime, etime, null, null, null);
            if (null == imageName || pojos.size() > 0)
                continue;
            boolean isSuccess = MyTimerTask.downImage(filePathName + "qx//" + strings[0].substring(0, 4) + ""
                    + strings[0].substring(4, 6) + "" + strings[0].substring(6), imgUrl, "10m风");
            tba.setTBA_WEAID(UUID.randomUUID().toString().replaceAll(" ", "-"));
            tba.setTBA_FILENAME(imageName);
            tba.setTBA_INFOTYPE(imgType);
            tba.setTBA_FILETYPE(imageName.substring(imageName.indexOf(".")));
            tba.setTBA_DESDATE(tm);
            tba.setTBA_GETDATE(tm);
            tba.setTBA_NOTE(String.valueOf(Integer.parseInt(hourStr)));
            if (isSuccess)
                tbaList.add(tba);
        }
        System.out.println("=========下载" + imgType + "图片结束===========");
        if (tbaList.size() > 0) {
            data.insertALL(tbaList);
        }
    }

    public void WenDaiWBC(String url, String stcd, String type, List<String> param, List<String> paramValue) {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<St_tide_rybPojo> array = new ArrayList<>();
        List<St_tide_rybPojo> ryblist = rybData.selectList(stcd, null, null, null, null, null, null, 0, 1);
        String ybtm = "";
        if (ryblist.size() > 0) {
            ybtm = ryblist.get(0).getYBTM();
        }
        String json = null;// getHtmlResourceByUrl(url, "UTF-8");
        StringBuilder sbu = new StringBuilder();
        sbu.append("发送").append(url).append("\n");
        // FileOutputStream outputStream = null;
        // try {
        // outputStream = new FileOutputStream(new File(filePathName +
        // "logs/myLog.txt"));
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
        try {
            json = wbcHttp(url, param, paramValue);
        } catch (IOException e) {
            e.printStackTrace();
            sbu.append(e.getMessage()).append("\n");
            // try {
            // outputStream.write(sbu.toString().getBytes());
            // outputStream.flush();
            // } catch (IOException ioException) {
            // ioException.printStackTrace();
            // }

            writeLogTxtStr(sbu.toString(), "WenDaiWBC" + formattedDateLog + ".txt");
        }
        sbu.append(json).append("\n");
        // try {
        // outputStream.write(sbu.toString().getBytes());
        // outputStream.flush();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        writeLogTxtStr(sbu.toString(), "WenDaiWBC" + formattedDateLog + ".txt");
        System.out.println(json);
        if (null == json)
            return;
        Map map = JSON.parseObject(json, Map.class);
        // System.out.println(map);
        if (map.containsKey("success")) {
            if ((Boolean) map.get("success") && map.containsKey("obj")) {
                String objStr = map.get("obj").toString();
                Map obj = JSON.parseObject(objStr, Map.class);
                String initialTime = "";
                if (obj.containsKey("initialTime")) {
                    initialTime = obj.get("initialTime").toString();
                }
                writeLogTxtStr("温带风暴潮数据的发生时间：" + initialTime, "WenDaiWBC" + formattedDateLog + ".txt");
                if (!"".equals(initialTime)) {
                    try {
                        Date dateTime = dateFormat.parse(initialTime);
                        initialTime = dateFormat.format(new Date(dateTime.getTime() + 8 * 60 * 60 * 1000));
                        dateTime = dateFormat.parse(initialTime);
                        // Random random = new Random();
                        // int i = random.nextInt(100) + 1;
                        // if (i <= 10) initialTime = dateFormat.format(new Date(dateTime.getTime() + 9
                        // * 60 * 60 * 1000));
                        String surges = obj.get("surges").toString();
                        List<Double> doubles = JSON.parseArray(surges, Double.class);
                        // System.out.println(doubles);
                        if (!"".equals(ybtm) && !ybtm.equals(initialTime)) {
                            int n = 0;
                            for (Double dou : doubles) {
                                St_tide_rybPojo rybPojo = new St_tide_rybPojo();
                                rybPojo.setSTCD(stcd);
                                rybPojo.setTDZ(dou / 100);
                                rybPojo.setTM(dateFormat.format(new Date(dateTime.getTime() + n * 60 * 60 * 1000)));
                                rybPojo.setYBTM(initialTime);
                                array.add(rybPojo);
                                n++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (array.size() > 0) {
            List<St_tide_rybPojo> arrayNew = TideDataInterpolator.interpolateHourlyTo5Min(array);
            System.out.println("=========开始导入" + type + "风暴潮数据===========");
            writeLogTxtStr("=========开始导入" + type + "风暴潮数据===========", "WenDaiWBC" + formattedDateLog + ".txt");
            Integer integer = rybData.insertALL(arrayNew);
            if (integer > 0) {
                System.out.println("=========导入" + type + "风暴潮数据完成，共导入" + integer + "条数据===========");
                writeLogTxtStr("=========导入" + type + "风暴潮数据完成，共导入" + integer + "条数据===========",
                        "WenDaiWBC" + formattedDateLog + ".txt");
            } else {
                System.out.println("=========导入" + type + "风暴潮数据失败===========");
                writeLogTxtStr("=========导入" + type + "风暴潮数据失败===========", "WenDaiWBC" + formattedDateLog + ".txt");
            }
        }
    }

    public void TaiFengWBC(String url, String stcd, String type, List<String> param, List<String> paramValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<St_tide_rybPojo> array = new ArrayList<>();
        List<St_tide_rybPojo> ryblist = rybData.selectList(stcd, null, null, null, null, null, null, 0, 1);
        String ybtm = "";
        if (ryblist.size() > 0) {
            ybtm = ryblist.get(0).getYBTM();
        }
        String json = null;// getHtmlResourceByUrl(url, "UTF-8");
        StringBuilder sbu = new StringBuilder();
        sbu.append("发送").append(url).append("\n");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePathName + "logs/myLogTF.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            json = wbcHttp(url, param, paramValue);
        } catch (IOException e) {
            e.printStackTrace();
            sbu.append(e.getMessage()).append("\n");
            try {
                outputStream.write(sbu.toString().getBytes());
                outputStream.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        sbu.append(json).append("\n");
        try {
            outputStream.write(sbu.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
        if (null == json)
            return;
        Map map = JSON.parseObject(json, Map.class);
        // System.out.println(map);
        if (map.containsKey("success")) {
            if ((Boolean) map.get("success") && map.containsKey("obj")) {
                String objStr = map.get("obj").toString();
                Map obj = JSON.parseObject(objStr, Map.class);
                if (obj.containsKey("data")) {
                    String data = obj.get("data").toString();
                    List<String> dataList = JSON.parseArray(data, String.class);
                    if (dataList.size() > 0) {
                        String dataObj = dataList.get(0);
                        if (dataList.size() > 1) {
                            dataObj = dataList.get(1);
                        }
                        Map dObj = JSON.parseObject(dataObj, Map.class);
                        String initialTime = "";
                        if (dObj.containsKey("initialTime")) {
                            initialTime = dObj.get("initialTime").toString();
                        }
                        if (!"".equals(initialTime)) {
                            try {
                                Date dateTime = dateFormat.parse(initialTime);
                                initialTime = dateFormat.format(new Date(dateTime.getTime()));
                                // Random random = new Random();
                                // int i = random.nextInt(100) + 1;
                                // if (i <= 10) initialTime = dateFormat.format(new Date(dateTime.getTime() + 9
                                // * 60 * 60 * 1000));
                                String surges = dObj.get("surges").toString();
                                List<Double> doubles = JSON.parseArray(surges, Double.class);
                                // System.out.println(doubles);
                                if (!ybtm.equals(initialTime)) {
                                    int n = 0;
                                    for (Double dou : doubles) {
                                        St_tide_rybPojo rybPojo = new St_tide_rybPojo();
                                        rybPojo.setSTCD(stcd);
                                        rybPojo.setTDZ(dou / 100);
                                        rybPojo.setTM(
                                                dateFormat.format(new Date(dateTime.getTime() + n * 60 * 60 * 1000)));
                                        rybPojo.setYBTM(initialTime);
                                        rybPojo.setRTYPE("台风风暴潮");
                                        array.add(rybPojo);
                                        n++;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if (array.size() > 0) {
            List<St_tide_rybPojo> arrayNew = TideDataInterpolator.interpolateHourlyTo5Min(array);
            arrayNew = new ArrayList<>(
                    arrayNew.stream()
                            .collect(Collectors.toMap(
                                    item -> item.getSTCD() + item.getTM() + item.getYBTM(), // Key 组合
                                    Function.identity(),
                                    (existing, replacement) -> existing // 保留首次出现的元素
                            ))
                            .values());

            System.out.println("=========开始导入" + type + "风暴潮数据===========");
            Integer integer = 0;
            int count = 2000;
            int num = arrayNew.size() / count;
            if (arrayNew.size() % count != 0) {
                num += 1;
            }
            List<St_tide_rybPojo> subList = null;
            for (int i = 0; i < num; i++) {
                if (i == num - 1) {
                    subList = arrayNew.subList(i * count, arrayNew.size());
                } else {
                    subList = arrayNew.subList(i * count, (i + 1) * count);
                }
                integer = rybData.insertALL(subList);
            }
            if (integer > 0) {
                System.out.println("=========导入" + type + "风暴潮数据完成，共导入" + integer + "条数据===========");
            } else {
                System.out.println("=========导入" + type + "风暴潮数据失败===========");
            }
        }
    }

    public void IncreaseWindyTask() {
        List<ST_STBPRP_BDto> bDtos = new ArrayList<>();// stbprpBData.GetSyncSTCD(null);
        ST_STBPRP_BDto dto1 = new ST_STBPRP_BDto();
        dto1.setSTCD("62701710");
        dto1.setLGTD(121.550729);
        dto1.setLTTD(31.384713);
        bDtos.add(dto1);
        ST_STBPRP_BDto dto2 = new ST_STBPRP_BDto();
        dto2.setSTCD("63401750");
        dto2.setLGTD(121.499195);
        dto2.setLTTD(31.382194);
        bDtos.add(dto2);
        ST_STBPRP_BDto dto3 = new ST_STBPRP_BDto();
        dto3.setSTCD("63405800");
        dto3.setLGTD(121.909317);
        dto3.setLTTD(30.861809);
        bDtos.add(dto3);
        for (ST_STBPRP_BDto dto : bDtos) {
            int nextInt = new Random().nextInt(500);
            String url = nodewindyUrl + "forecast/v2.4/ecmwf/";// "https://node.windy.com/forecast/v2.4/ecmwf/";
            url = url + dto.getLTTD() + "/" + dto.getLGTD();// + "?token=&token2=pending&sc=1&poc=" + nextInt;
            System.out.println(url);
            // String json = getHtmlResourceByUrl(url, "UTF-8");
            String[] paramArr = { "token", "token2", "sc", "poc" };
            String[] paramValArr = { "", "pending", "1", "" + nextInt };
            List<String> param = Arrays.asList(paramArr);
            List<String> paramVal = Arrays.asList(paramValArr);
            StringBuilder builder = new StringBuilder();
            builder.append("发送：" + url + "?token=&token2=pending&sc=1&poc=" + nextInt).append("\n");
            String json = null;
            try {
                json = wbcHttp(url, param, paramVal);
            } catch (IOException e) {
                e.printStackTrace();
                builder.append("error信息：").append(e.getMessage()).append("\n");
            }
            builder.append(json).append("\n");
            try {
                byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
                FileOutputStream out = new FileOutputStream(filePathName + "/logs/WindyTask.txt");
                out.write(bytes);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (json == null)
                return;
            Map map = JSON.parseObject(json, Map.class);
            List<St_windyweater_rPojo> windObjList = new ArrayList<>();
            String refTime = "", update = "";
            List<String> windList = new ArrayList<>();
            List<String> windDirList = new ArrayList<>();
            List<String> pressureList = new ArrayList<>();
            List<String> mmList = new ArrayList<>();
            List<String> origDateList = new ArrayList<>();
            List<String> gustList = new ArrayList<>();
            if (map.containsKey("header")) {
                String header = map.get("header").toString();
                Map headerMap = JSON.parseObject(header, Map.class);
                if (headerMap.containsKey("refTime")) {
                    refTime = headerMap.get("refTime").toString();
                    LocalDateTime time = LocalDateTime.parse(refTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    refTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                if (headerMap.containsKey("update")) {
                    update = headerMap.get("update").toString();
                    LocalDateTime time = LocalDateTime.parse(update, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    update = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            }
            if (map.containsKey("data")) {
                String dataJson = map.get("data").toString();
                Map dataMap = JSON.parseObject(dataJson, Map.class);
                if (dataMap.containsKey("wind")) {
                    String wind = dataMap.get("wind").toString();
                    windList = JSON.parseArray(wind, String.class);
                }
                if (dataMap.containsKey("windDir")) {
                    String windDir = dataMap.get("windDir").toString();
                    windDirList = JSON.parseArray(windDir, String.class);
                }
                if (dataMap.containsKey("pressure")) {
                    String pressure = dataMap.get("pressure").toString();
                    pressureList = JSON.parseArray(pressure, String.class);
                }
                if (dataMap.containsKey("mm")) {
                    String pressure = dataMap.get("mm").toString();
                    mmList = JSON.parseArray(pressure, String.class);
                }
                if (dataMap.containsKey("origDate")) {
                    String pressure = dataMap.get("origDate").toString();
                    origDateList = JSON.parseArray(pressure, String.class);
                }
                if (dataMap.containsKey("gust")) {
                    String pressure = dataMap.get("gust").toString();
                    gustList = JSON.parseArray(pressure, String.class);
                }
            }
            for (int i = 0; i < origDateList.size(); i++) {
                St_windyweater_rPojo wind = new St_windyweater_rPojo();
                String tm = origDateList.get(i);
                LocalDateTime time = LocalDateTime.parse(tm, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                tm = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                wind.setSTCD(dto.getSTCD());
                wind.setTM(tm);
                wind.setYBTM(refTime);
                wind.setUPDATETM(update);
                wind.setWIND(Double.valueOf(windList.get(i)));
                wind.setWINDDIR(Double.valueOf(windDirList.get(i)));
                wind.setPRESSURE(Double.parseDouble(pressureList.get(i)) / 100);
                wind.setDRP(Double.valueOf(mmList.get(i)));
                wind.setGUST(Double.valueOf(gustList.get(0)));
                long hour = 3;
                if (i < origDateList.size() - 1) {
                    LocalDateTime t1 = LocalDateTime.parse(origDateList.get(i + 1),
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    Date dateH = Date.from(t1.atZone(ZoneId.systemDefault()).toInstant());
                    LocalDateTime t2 = LocalDateTime.parse(origDateList.get(i), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    Date date = Date.from(t2.atZone(ZoneId.systemDefault()).toInstant());
                    hour = (dateH.getTime() - date.getTime()) / (60 * 60 * 1000);
                    wind.setTYPE(String.valueOf(hour));
                } else {
                    wind.setTYPE(String.valueOf(hour));
                }
                windObjList.add(wind);
            }
            // System.out.println("============条数"+windObjList.size()+"================");
            List<St_windyweater_rPojo> windyList = windData.selectList(dto.getSTCD(), null, refTime, refTime, null, 0,
                    1);
            if (windyList.size() > 0) {
                if (!windyList.get(0).getUPDATETM().equals(update)) {
                    Integer delnum = windData.deleteALLBySTCDAndYBTM(dto.getSTCD(), refTime);
                    if (delnum > 0) {
                        System.out.println("===========开始同步windy风数据============");
                        Integer integer = windData.insertALL(windObjList);
                        System.out.println("===========同步windy风数据结束，共" + integer + "条数据============");
                    }
                }
            } else {
                System.out.println("===========开始同步windy风数据============");
                Integer integer = windData.insertALL(windObjList);
                System.out.println("===========同步windy风数据结束，共" + integer + "条数据============");
            }
        }
    }

    public static String ZGHYYBWaterPostHttp(String paramValue) throws IOException {
        MyTimerTask myTimerTask = ApplicationContextUtil.getApplicationContext().getBean(MyTimerTask.class);
        String url = myTimerTask.oceanguideUrl + "hyyj2/forecast/nearAgingReport";// "https://www.oceanguide.org.cn/hyyj2/forecast/nearAgingReport";
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("areaName", paramValue)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String wbcHttp(String url, List<String> param, List<String> paramValue) throws IOException {
        OkHttpClient client = new OkHttpClient();
        StringBuilder sbu = new StringBuilder();
        // FormBody.Builder builder = new FormBody.Builder();
        if (null != param) {
            sbu.append("?");
            for (int i = 0; i < param.size(); i++) {
                // builder.add(param.get(i), paramValue.get(i));
                sbu.append(param.get(i)).append("=").append(paramValue.get(i)).append("&");
            }
        }
        // FormBody body = builder.build();
        // Map<String,String> map = new HashMap<>();
        // map.put("Accept","application/x-www-form-urlencoded");
        // Headers headers = Headers.of(map);
        Request request = new Request.Builder()
                .url(url + sbu.toString())
                // .headers(headers)
                // .addHeader("Accept","application/x-www-form-urlencoded")
                // .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String PostHttp(String url, List<String> param, List<String> paramValue) throws IOException {
        OkHttpClient client = new OkHttpClient();
        // StringBuilder sbu = new StringBuilder();
        FormBody.Builder builder = new FormBody.Builder();
        if (null != param) {
            // sbu.append("?");
            for (int i = 0; i < param.size(); i++) {
                builder.add(param.get(i), paramValue.get(i));
                // sbu.append(param.get(i)).append("=").append(paramValue.get(i)).append("&");
            }
        }
        FormBody body = builder.build();
        // Map<String,String> map = new HashMap<>();
        // map.put("Accept","application/x-www-form-urlencoded");
        // Headers headers = Headers.of(map);
        Request request = new Request.Builder()
                .url(url)
                // .headers(headers)
                // .addHeader("Accept","application/x-www-form-urlencoded")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void ZGHYYBWaterTask() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> paramList = Arrays.asList("E17,E18".split(","));
        for (String str : paramList) {
            try {
                String ybtm = "";
                List<St_tide_rybPojo> rybList = new ArrayList<>();
                String postResult = ZGHYYBWaterPostHttp(str);
                List<St_tide_rybPojo> rybPojos = rybData.selectList(str, null, null, null, null, null, null, 0, 1);
                if (rybPojos.size() > 0) {
                    ybtm = rybPojos.get(0).getYBTM();
                }
                List<String> waterStageList = new ArrayList<>();
                List<String> timeList = new ArrayList<>();
                Map map = JSON.parseObject(postResult, Map.class);
                boolean isUPdate = true;
                if (map.containsKey("success") && (boolean) map.get("success")) {
                    if (map.containsKey("obj")) {
                        String objStr = map.get("obj").toString();
                        Map objMap = JSON.parseObject(objStr, Map.class);
                        if (objMap.containsKey("waterStage")) {
                            String waterStage = objMap.get("waterStage").toString();
                            Map waterStageMap = JSON.parseObject(waterStage, Map.class);
                            if (waterStageMap.containsKey("data")) {
                                String data = waterStageMap.get("data").toString();
                                Map dataMap = JSON.parseObject(data, Map.class);
                                if (dataMap.containsKey("waterStage")) {
                                    String waterStageStr = dataMap.get("waterStage").toString();
                                    waterStageList = JSON.parseArray(waterStageStr, String.class);
                                }
                            }
                            if (waterStageMap.containsKey("time")) {
                                String time = waterStageMap.get("time").toString();
                                timeList = JSON.parseArray(time, String.class);
                            }
                        }
                    }
                }
                if (timeList.size() > 0) {
                    String tm = timeList.get(0);
                    tm = format.format(new Date()) + "-" + tm.replace("日", " ").replace("时", ":") + "00:00";
                    String YbTm = dateFormat.format(new Date(dateFormat.parse(tm).getTime() - 60 * 60 * 1000));
                    System.out.println("===========" + ybtm + ":" + YbTm + "=============");
                    if (!ybtm.equals(YbTm)) {
                        isUPdate = false;
                    }
                    int compareTo = tm.compareTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date(new Date().getTime() + 24 * 60 * 60 * 1000)));
                    if (compareTo < 0) {
                        for (int i = 0; i < timeList.size(); i++) {
                            String waterVal = waterStageList.get(i);
                            if (null != waterVal) {
                                St_tide_rybPojo rybPojo = new St_tide_rybPojo();
                                rybPojo.setSTCD(str);
                                rybPojo.setTDZ(Double.parseDouble(waterVal) / 100);
                                rybPojo.setTM(dateFormat
                                        .format(new Date(dateFormat.parse(tm).getTime() + i * 60 * 60 * 1000)));// 加iH
                                rybPojo.setYBTM(
                                        dateFormat.format(new Date(dateFormat.parse(tm).getTime() - 60 * 60 * 1000)));// 减1H
                                // System.out.println(rybPojo.getTM());
                                // System.out.println(rybPojo.getTM());
                                rybList.add(rybPojo);
                            }
                        }
                    }
                }
                if (!isUPdate) {
                    List<St_tide_rybPojo> rybListNew = TideDataInterpolator.interpolateHourlyTo5Min(rybList);
                    System.out.println("============开始同步国家海洋预报增水数据=============");
                    rybData.insertALL(rybListNew);
                    System.out.println("============同步国家海洋预报增水数据结束，共" + rybList.size() + "条数据=============");
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void ZGHYYBWaterTask47(String stcd, String rtype) {
        writeLogTxtStr("开始跑ZGHYYBWaterTask47接口，类型：" + rtype, "ZGHYYBWaterTask47.txt");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> paramList = Arrays.asList(stcd.split(","));
        String STIME = dateFormat.format(new Date(new Date().getTime() - 240 * 60 * 60 * 1000));
        String ETIME = dateFormat.format(new Date(new Date().getTime() - 0 * 60 * 60 * 1000));
        for (String str : paramList) {
            String parmasMap = "{\"STCD\":\"" + str + "\",\"STIME\":\"" + STIME + "\",\"ETIME\":\"" + ETIME
                    + "\",\"RTYPE\":\"" + rtype + "\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");

            String apiUrl = ServerIP47 + "/json/reply/DATA_ST_TIDE_RYBSelFirst";
            writeLogTxtStr("result" + rtype + "接口地址：" + apiUrl + "，************参数：" + parmasMap,
                    "ZGHYYBWaterTask47.txt");
            String result = apihelper.apipost(ServerIP47 + "/json/reply/DATA_ST_TIDE_RYBSelFirst", parmasMap, header);
            System.out.println("result" + rtype + "结果：" + result);
            writeLogTxtStr("result" + rtype + "结果：" + result, "ZGHYYBWaterTask47.txt");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> mapList = new HashMap<>();
            String ybtm = "";
            List<St_tide_rybPojo> rybPojos = rybData.selectList(str, null, null, null, null, null, null, 0, 1);
            if (rybPojos.size() > 0) {
                ybtm = rybPojos.get(0).getYBTM();
            }
            try {
                mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
                });
                int total = (int) mapList.get("total");
                if (total > 0) {
                    List<St_tide_rybPojo> list = new ArrayList<>();
                    List<Map<String, Object>> resultData = (List<Map<String, Object>>) mapList.get("data");
                    for (Map<String, Object> map : resultData) {
                        St_tide_rybPojo pojo = new St_tide_rybPojo();
                        pojo.setSTCD(map.get("STCD").toString());
                        pojo.setTM(map.get("TM").toString());
                        pojo.setYBTM(map.get("YBTM").toString());
                        if (map.get("TDZ") != null) {
                            pojo.setTDZ(Double.parseDouble(map.get("TDZ").toString()));
                        } else {
                            System.out.println("TDZ值" + map.get("TDZ"));
                        }
                        pojo.setRTYPE(map.get("RTYPE").toString());
                        if (map.get("NOTE") != null) {
                            pojo.setNOTE(map.get("NOTE").toString());
                        }
                        if (map.get("REMARK") != null) {
                            pojo.setREMARK(map.get("REMARK").toString());
                        }
                        list.add(pojo);
                    }
                    if (list.size() > 0) {
                        boolean isUPdate = true;
                        String YbTm = list.get(0).getYBTM();
                        System.out.println("===========" + ybtm + ":" + YbTm + "=============");
                        if (!ybtm.equals(YbTm)) {
                            isUPdate = false;
                        }

                        if (isUPdate) {// 存在过了
                            rybData.updateALL(list);
                        } else {
                            rybData.insertALL(list);
                        }
                    }
                    writeLogTxtStr("ZGHYYBWaterTask47接口返回结果是：" + result, "ZGHYYBWaterTask47.txt");
                } else {
                    writeLogTxtStr("ZGHYYBWaterTask47错，接口返回结果是：" + result, "ZGHYYBWaterTask47.txt");
                }
            } catch (IOException e) {
                writeLogTxtStr("ZGHYYBWaterTask47错，调用" + parmasMap + "接口报错：" + e.getMessage(), "ZGHYYBWaterTask47.txt");
            }
        }
    }

    public void TaiFengWBC() {
        List<Map<String, String>> mapList = new ArrayList<>();
        List<Map<String, String>> mapListTwo = new ArrayList<>();
        Map<String, String> lcgObj = new HashMap<>();
        Map<String, String> jszObj = new HashMap<>();
        Map<String, String> wskObj = new HashMap<>();
        Map<String, String> gqObj = new HashMap<>();
        Map<String, String> dls = new HashMap<>();
        lcgObj.put("STCD", "63405800");
        lcgObj.put("STNM", "芦潮港");
        lcgObj.put("ZD", "芦潮港");
        mapList.add(lcgObj);
        jszObj.put("STCD", "63405900");
        jszObj.put("STNM", "金山嘴");
        jszObj.put("ZD", "金山嘴");
        mapList.add(jszObj);
        wskObj.put("STCD", "63401750");
        wskObj.put("STNM", "吴淞口");
        wskObj.put("ZD", "吴淞");
        mapList.add(wskObj);
        gqObj.put("STCD", "62701710");
        gqObj.put("STNM", "高桥");
        gqObj.put("ZD", "高桥");
        mapList.add(gqObj);
        dls.put("STCD", "DLS");
        dls.put("STNM", "灯笼山");
        dls.put("ZD", "灯笼山");
        mapListTwo.add(lcgObj);
        mapListTwo.add(dls);
        List<ZJ_TFPojo> tfPojos = zj_tfData.selectList(null, null, null, null, Collections.singletonList("1"), null,
                null, null);
        List<String> codeList = new ArrayList<>();
        String code = "";
        if (tfPojos.size() > 0) {
            for (ZJ_TFPojo obj : tfPojos) {
                code = "TY" + obj.getZJ_TFBH().trim().substring(2);
                codeList.add(code);
            }
        }
        for (Map<String, String> map : mapList) {
            for (String codeStr : codeList) {
                String url = nmefcUrl + "api/data/typhoon/statistics";
                String[] paramArr = { "site", "tyCode" };
                String[] paramValArr = { map.get("ZD"), codeStr };// "TY2306"
                List<String> param = Arrays.asList(paramArr);
                List<String> paramVal = Arrays.asList(paramValArr);
                TaiFengWBC(url, map.get("STCD"), "台风", param, paramVal);
            }
        }
        for (Map<String, String> map : mapListTwo) {
            for (String codeStr : codeList) {
                String url = nmefcUrl + "api/data/typhoon/statistics";
                String[] paramArr = { "site", "tyCode" };
                System.out.println(code);
                String[] paramValArr = { map.get("ZD"), codeStr };// "TY2306"
                List<String> param = Arrays.asList(paramArr);
                List<String> paramVal = Arrays.asList(paramValArr);
                TaiFengJsonTask(url, map.get("STCD"), "台风", param, paramVal);
            }
        }
    }

    public void XHWater(List<Map<String, String>> mapList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat dateFormatTwo = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, String> map : mapList) {
            List<St_rnfl_fPojo> rnflFPojos = new ArrayList<>();
            String url = nmcUrl + "publish/forecast/ASH/" + map.get("PATH") + ".html";
            St_rnfl_fPojo fPojo = rnflFData.selectByYBTMNewByStcd(map.get("STCD"));
            String ybtm = fPojo.getYBTM();
            String html = getHtmlResourceByUrl(url, "UTF-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("col-xs-8");
            String fbtm = dateFormat.format(new Date()) + elements.get(1).children().get(1).html().replace("发布时间：", "-")
                    + ":00";

            if (!ybtm.equals(fbtm)) {
                Element element = document.getElementById("hourValues");
                List<Node> nodeList = element.child(0).childNodes();// dayList
                for (Node node : nodeList) {
                    List<Node> nodes = node.childNodes();// hourList
                    int id = Integer.parseInt(node.attr("id").replace("day", ""));
                    for (Node n : nodes) {
                        // System.out.println(n.toString());
                        String time = n.childNode(0).toString()
                                .substring("<div>".length(), n.childNode(0).toString().indexOf("</div>")).trim();
                        String date = "";
                        if (!n.attr("class").contains("hbg")) {
                            time = time.substring(time.indexOf("日") + 1) + ":00";
                            date = dateFormatTwo.format(new Date(new Date().getTime() + (id + 1) * 24 * 60 * 60 * 1000))
                                    + " " + time;
                        } else {
                            time = time + ":00";
                            date = dateFormatTwo.format(new Date(new Date().getTime() + id * 24 * 60 * 60 * 1000)) + " "
                                    + time;
                        }
                        // System.out.println(date);
                        String src = n.childNode(1).childNode(0).attr("src");
                        String jy = n.childNode(2).toString()
                                .substring("<div>".length(), n.childNode(2).toString().indexOf("</div>")).trim();
                        String qw = n.childNode(3).toString().substring("<div class=\"tmp_lte_40\">".length(),
                                n.childNode(3).toString().indexOf("</div>")).trim();
                        String fs = n.childNode(4).toString()
                                .substring("<div>".length(), n.childNode(4).toString().indexOf("</div>")).trim();
                        String fx = n.childNode(5).toString()
                                .substring("<div>".length(), n.childNode(5).toString().indexOf("</div>")).trim();
                        String qy = n.childNode(6).toString()
                                .substring("<div class=\"hide\">".length(), n.childNode(6).toString().indexOf("</div>"))
                                .trim();
                        String sd = n.childNode(7).toString()
                                .substring("<div>".length(), n.childNode(7).toString().indexOf("</div>")).trim();
                        String yl = n.childNode(8).toString()
                                .substring("<div class=\"hide\">".length(), n.childNode(8).toString().indexOf("</div>"))
                                .trim();
                        // System.out.println(qy);
                        St_rnfl_fPojo rnflFPojo = new St_rnfl_fPojo();
                        rnflFPojo.setTM(date);
                        rnflFPojo.setWEATHERCODE(src);
                        rnflFPojo.setDRP(Double.valueOf(jy.equals("-") ? "0" : jy.substring(0, jy.indexOf("mm"))));
                        rnflFPojo.setTEMP(Double.valueOf(qw.equals("-") ? "0" : qw.substring(0, qw.indexOf("℃"))));
                        rnflFPojo.setWINDSPEED(
                                Double.valueOf(fs.equals("-") ? "0" : fs.substring(0, fs.indexOf("m/s"))));
                        rnflFPojo.setWINDDIR(fx);
                        rnflFPojo.setAIRPRESSURE(
                                Double.valueOf(qy.equals("-") ? "0" : qy.substring(0, qy.indexOf("hPa"))));
                        rnflFPojo.setHUMIDITY(Double.valueOf(sd.equals("-") ? "0" : sd.substring(0, sd.indexOf("%"))));
                        rnflFPojo.setYBTM(fbtm);
                        rnflFPojo.setSTCD(map.get("STCD"));
                        rnflFPojo.setTYPE(map.get("TYPE"));
                        rnflFPojo.setINTV(3.0);
                        if (!rnflFPojos.contains(rnflFPojo)) {
                            rnflFPojos.add(rnflFPojo);
                        }
                    }
                }
            }
            System.out.println("============开始同步" + map.get("TYPE") + "降雨数据============");
            if (rnflFPojos.size() > 0) {
                rnflFData.insertALL(rnflFPojos);
            }
            System.out.println("============同步" + map.get("TYPE") + "降雨数据结束，共" + rnflFPojos.size() + "条数据============");
        }

    }

    public void FQWater() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        //
        StringBuilder builder = new StringBuilder();
        //
        String filePath = filePathName + "fq/FQ";
        String filePathNC = filePathName + "FQNC/FQNC";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        // LocalDateTime dateTime =
        // LocalDateTime.of(now.getYear(),now.getMonth(),1,0,0,0).toLocalDate().atStartOfDay();
        // long ms = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        LocalDateTime dateTime = now.minusHours(168); // 首次同步放宽到7天，稳定后改回12
        Date dateTimedate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        writeLogTxtStr("方法FQWater(开始下载ftp文件)", "FQWater" + formattedDateLog + ".txt");
        // downGRB(filePath,FtpIP, FtpPort, "swzz",
        // "zxt@18SH",dateTimedate);//下载202.96.202.173 100.97.232.125
        downGRB6(filePath, NewFtpIP, NewFtpPort, NewFtpUser, NewFtpPass, NewFtpDir48, dateTimedate);// 新FTP 48h er01_csj
        File file = new File(filePath);
        File[] files = file.listFiles();
        File fileNC = new File(filePathNC);
        // if (!fileNC.exists()) fileNC.mkdirs();
        writeLogTxtStr("方法FQWater(从ftp下载了" + files.length + "个文件)", "FQWater" + formattedDateLog + ".txt");
        try {
            for (File f : files) {
                String fileName = f.getName();
                builder.append(fileName).append("\n");
                File ncf = new File(filePathNC + "/" + fileName.substring(0, fileName.indexOf(".")) + ".nc");
                if (ncf.exists())
                    continue;
                if (fileName.contains("GRB2")) {
                    Runtime runtime = Runtime.getRuntime();
                    String grb = filePath + "/" + fileName;
                    String nc = filePathNC + "/" + fileName.substring(0, fileName.indexOf(".")) + ".nc";
                    // runtime.exec("chmod +x /home/wsupport/Downloads/wgrib2/grib2/wgrib2");
                    // wgrib2 可执行文件路径 /home/wsupport/Downloads/wgrib2/grib2/wgrib2/wgrib2

                    writeLogTxtStr("方法FQWater(开始执行转换nc文件命令)" + nc, "FQWater" + formattedDateLog + ".txt");
                    Process exec = runtime.exec(wgrib2Path + " " + grb + " -netcdf " + nc);
                    int exitCode = exec.waitFor();
                    builder.append("wgrib2 ").append(grb).append(" -netcdf ").append(nc).append("\n");
                    if (exitCode != 0) {
                        // wgrib2转换失败，删除损坏的GRB2和NC，下次重新下载
                        writeLogTxtStr("方法FQWater(wgrib2转换失败 exitCode=" + exitCode + "，删除损坏文件)" + grb,
                                "FQWater" + formattedDateLog + ".txt");
                        f.delete();
                        new File(nc).delete();
                    } else {
                        writeLogTxtStr("方法FQWater(grb转nc文件成功)" + nc, "FQWater" + formattedDateLog + ".txt");
                    }
                }
            }
        } catch (Exception e) {
            builder.append("error:").append(e.getMessage()).append("\n");

            writeLogTxtStr("方法FQWater报错" + e.getMessage(), "FQWater" + formattedDateLog + ".txt");
        }
        try {
            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/GrbToNcList.txt");
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] NCfiles = fileNC.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (null != NCfiles) {
            fileNameList = Arrays.stream(NCfiles).filter(m -> {
                String name = m.getName();
                return name.contains(".nc");
            }).map(File::getName).collect(Collectors.toList());
        }
        for (String name : fileNameList) {
            writeLogTxtStr("方法FQWater(文件" + name + ")", "FQWater" + formattedDateLog + ".txt");
            List<Map<String, Object>> mapList = new ArrayList<>();
            if (!(name.contains("P_RFFC_SPCC-ER01-CSJ_") || name.contains("P_RFFC_SPCC-ER03-CSJ_"))) {
                continue;
            }
            StringBuilder sbd = new StringBuilder();
            int strIndex = name.contains("P_RFFC_SPCC-ER01-CSJ_") ? name.indexOf("P_RFFC_SPCC-ER01-CSJ_")
                    : name.indexOf("P_RFFC_SPCC-ER03-CSJ_");
            String TM = name.substring(strIndex + 21, strIndex + 21 + 12);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long millis = calendar.getTimeInMillis();
            try {
                date = df.parse(TM);
                TM = dateFormat.format(date);
            } catch (ParseException e) {
                writeLogTxtStr("方法FQWater(转换时间报错" + TM + ")", "FQWater" + formattedDateLog + ".txt");
                e.printStackTrace();
            }
            if (date.getTime() < millis) {
                continue;
            }
            String FPDR = name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("_") + 4);
            System.out.println("FPDR:::::" + FPDR);
            writeLogTxtStr("方法FQWater(FPDR:::::" + FPDR + ")", "FQWater" + formattedDateLog + ".txt");
            if (!FPDR.equals("048")) {
                continue;
            }
            // 检查是否已入库：同名文件跳过，同TM+同集合成员不同文件=预报更新
            boolean isUpdate = false;
            List<String> typeList = Arrays.asList("48");
            List<Tz_ncfilePojo> tzNcfileList = ncfileData.selectList(null, null, TM, TM, typeList, null, null);
            if (tzNcfileList.size() > 0) {
                String existNcFile = tzNcfileList.get(0).getNCFILE();
                if (existNcFile != null && existNcFile.equals(name)) {
                    continue; // 同一个文件已处理过
                }
                // 提取新旧文件的集合成员（ER01/ER03），不同成员不互相覆盖
                String newEns = name.contains("ER01") ? "ER01" : (name.contains("ER03") ? "ER03" : "");
                String oldEns = existNcFile.contains("ER01") ? "ER01" : (existNcFile.contains("ER03") ? "ER03" : "");
                if (!newEns.isEmpty() && !newEns.equals(oldEns)) {
                    continue; // 不同集合成员，保留已有数据不覆盖
                }
                // 同一起报时间+同集合成员的新文件 → 预报更新
                writeLogTxtStr("方法FQWater(检测到更新预报，旧文件:" + existNcFile + " → 新文件:" + name + ")",
                        "FQWater" + formattedDateLog + ".txt");
                tzNcfileList.get(0).setNCFILE(name);
                ncfileData.updateOne(tzNcfileList.get(0));
                isUpdate = true;
            }
            String Hour = name.substring(name.lastIndexOf("_") + 4, name.lastIndexOf("_") + 6);
            System.out.println("==========开始解析:" + name + "=================");
            sbd.append("==========开始解析:").append(name).append("=================\n");
            writeLogTxtStr("方法FQWater(==========开始解析：" + name + ")", "FQWater" + formattedDateLog + ".txt");
            NetcdfFile openFile = NetcdfDataset.openDataset(filePathNC + File.separatorChar + name);
            List<Variable> variables = openFile.getVariables();
            double[] dataArray = null;
            List<Double> latList = new ArrayList<>();
            List<Double> lonList = new ArrayList<>();
            List<Double> timeList = new ArrayList<>();
            for (Variable variable : variables) {
                String fullName = variable.getFullName();
                System.out.println("==========fullName:" + fullName + "=================");

                Array data = variable.read();
                switch (fullName) {
                    case "latitude":
                        latList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    case "longitude":
                        lonList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    case "time":
                        timeList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    default:
                        dataArray = toDoubleArray(data);
                        break;
                }
            }
            int latSize = latList.size();
            int lonSize = lonList.size();
            System.out.println(
                    latSize + ";" + lonSize + ";" + timeList.size() + ";" + (dataArray != null ? dataArray.length : 0));
            for (int i = 0; i < timeList.size(); i++) {
                long s = timeList.get(i).longValue();
                Instant startTime = Instant.ofEpochSecond(s);
                LocalDateTime time = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
                time = time.minusHours(8);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String FTime = time.format(formatter);

                Map<String, Object> map = new HashMap<>();
                map.put("RLSTM", TM);
                map.put("FTM", FTime);
                map.put("dataArray", dataArray);
                map.put("sliceOffset", i * latSize * lonSize);
                map.put("hour", i);
                map.put("FPDR", FPDR);
                map.put("HOURS", Hour);
                mapList.add(map);
            }
            // String s = sbd.toString();
            // byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            // FileOutputStream out = new FileOutputStream(new
            // File("D:\\work\\UploadDoc\\log\\"+name.substring(0,name.indexOf("."))+"_log.txt"));
            // out.write(bytes);
            // out.close();

            writeLogTxtStr("方法FQWater(解析完成，开始入库)", "FQWater" + formattedDateLog + ".txt");
            if (!isUpdate) {
                insertData(mapList, name, TM, Integer.parseInt(FPDR));
            }
            insertDataWater(mapList, latList.size(), lonList.size());
        }
    }

    public void FQ336HourWater() throws IOException {
        //
        StringBuilder builder = new StringBuilder();
        //
        String filePath = filePathName + "fq/FQ";
        String filePathNC = filePathName + "FQNC/FQNC";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        // LocalDateTime dateTime =
        // LocalDateTime.of(now.getYear(),now.getMonth(),1,0,0,0).toLocalDate().atStartOfDay();
        // long ms = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        LocalDateTime dateTime = now.minusHours(168); // 首次同步放宽到7天，稳定后改回12
        Date dateTimedate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        downGRB6(filePath, NewFtpIP, NewFtpPort, NewFtpUser, NewFtpPass, NewFtpDir336, dateTimedate);// 新FTP 336h er03_csj
        File file = new File(filePath);
        File[] files = file.listFiles();
        File fileNC = new File(filePathNC);
        // if (!fileNC.exists()) fileNC.mkdirs();
        try {
            for (File f : files) {
                String fileName = f.getName();
                builder.append(fileName).append("\n");
                File ncf = new File(filePathNC + "/" + fileName.substring(0, fileName.indexOf(".")) + ".nc");
                if (ncf.exists())
                    continue;
                if (fileName.contains("GRB2")) {
                    Runtime runtime = Runtime.getRuntime();
                    String grb = filePath + "/" + fileName;
                    String nc = filePathNC + "/" + fileName.substring(0, fileName.indexOf(".")) + ".nc";
                    Process exec = runtime.exec(wgrib2Path + " " + grb + " -netcdf " + nc);
                    int exitCode = exec.waitFor();
                    builder.append("wgrib2 ").append(grb).append(" -netcdf ").append(nc).append("\n");
                    if (exitCode != 0) {
                        // wgrib2转换失败，删除损坏的GRB2和NC，下次重新下载
                        writeLogTxtStr("方法FQ336HourWater(wgrib2转换失败 exitCode=" + exitCode + "，删除损坏文件)" + grb,
                                "FQWater.txt");
                        f.delete();
                        new File(nc).delete();
                    }
                }
            }
        } catch (Exception e) {
            builder.append("error:").append(e.getMessage()).append("\n");
        }
        try {
            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/GrbToNcList.txt");
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] NCfiles = fileNC.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (null != NCfiles) {
            fileNameList = Arrays.stream(NCfiles).filter(m -> {
                String name = m.getName();
                return name.contains(".nc");
            }).map(File::getName).collect(Collectors.toList());
        }
        for (String name : fileNameList) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            if (!(name.contains("P_RFFC_SPCC-ER01-CSJ_") || name.contains("P_RFFC_SPCC-ER03-CSJ_"))) {
                continue;
            }
            StringBuilder sbd = new StringBuilder();
            int strIndex = name.contains("P_RFFC_SPCC-ER01-CSJ_") ? name.indexOf("P_RFFC_SPCC-ER01-CSJ_")
                    : name.indexOf("P_RFFC_SPCC-ER03-CSJ_");
            String TM = name.substring(strIndex + 21, strIndex + 21 + 12);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long millis = calendar.getTimeInMillis();
            try {
                date = df.parse(TM);
                TM = dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date.getTime() < millis) {
                continue;
            }
            String FPDR = name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("_") + 4);
            System.out.println("FPDR:::::" + FPDR);
            if (!FPDR.equals("336")) {
                continue;
            }
            // 检查是否已入库：同名文件跳过，同TM+同集合成员不同文件=预报更新
            boolean isUpdate = false;
            List<String> typeList = Arrays.asList("336");
            List<Tz_ncfilePojo> tzNcfileList = ncfileData.selectList(null, null, TM, TM, typeList, null, null);
            if (tzNcfileList.size() > 0) {
                String existNcFile = tzNcfileList.get(0).getNCFILE();
                if (existNcFile != null && existNcFile.equals(name)) {
                    continue; // 同一个文件已处理过
                }
                // 提取新旧文件的集合成员（ER01/ER03），不同成员不互相覆盖
                String newEns = name.contains("ER01") ? "ER01" : (name.contains("ER03") ? "ER03" : "");
                String oldEns = existNcFile.contains("ER01") ? "ER01" : (existNcFile.contains("ER03") ? "ER03" : "");
                if (!newEns.isEmpty() && !newEns.equals(oldEns)) {
                    continue; // 不同集合成员，保留已有数据不覆盖
                }
                // 同一起报时间+同集合成员的新文件 → 预报更新
                writeLogTxtStr("方法FQ336HourWater(检测到更新预报，旧文件:" + existNcFile + " → 新文件:" + name + ")",
                        "FQWater.txt");
                tzNcfileList.get(0).setNCFILE(name);
                ncfileData.updateOne(tzNcfileList.get(0));
                isUpdate = true;
            }
            String Hour = name.substring(name.lastIndexOf("_") + 4, name.lastIndexOf("_") + 6);
            System.out.println("==========336开始解析:" + name + "=================");
            sbd.append("==========336开始解析:").append(name).append("=================\n");
            NetcdfFile openFile = NetcdfDataset.openDataset(filePathNC + File.separatorChar + name);
            List<Variable> variables = openFile.getVariables();
            double[] dataArray = null;
            List<Double> latList = new ArrayList<>();
            List<Double> lonList = new ArrayList<>();
            List<Double> timeList = new ArrayList<>();
            for (Variable variable : variables) {
                String fullName = variable.getFullName();
                System.out.println("==========fullName:" + fullName + "=================");

                Array data = variable.read();
                switch (fullName) {
                    case "latitude":
                        latList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    case "longitude":
                        lonList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    case "time":
                        timeList = readArrayAsListStringTokenizer(data.toString(), fullName);
                        break;
                    default:
                        dataArray = toDoubleArray(data);
                        break;
                }
            }
            int latSize = latList.size();
            int lonSize = lonList.size();
            System.out.println(
                    latSize + ";" + lonSize + ";" + timeList.size() + ";" + (dataArray != null ? dataArray.length : 0));
            for (int i = 0; i < timeList.size(); i++) {
                long s = timeList.get(i).longValue();
                Instant startTime = Instant.ofEpochSecond(s);
                LocalDateTime time = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
                time = time.minusHours(8);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String FTime = time.format(formatter);

                Map<String, Object> map = new HashMap<>();
                map.put("RLSTM", TM);
                map.put("FTM", FTime);
                map.put("dataArray", dataArray);
                map.put("sliceOffset", i * latSize * lonSize);
                map.put("hour", i);
                map.put("FPDR", FPDR);
                map.put("HOURS", Hour);
                mapList.add(map);
            }
            // String s = sbd.toString();
            // byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            // FileOutputStream out = new FileOutputStream(new
            // File("D:\\work\\UploadDoc\\log\\"+name.substring(0,name.indexOf("."))+"_log.txt"));
            // out.write(bytes);
            // out.close();
            if (!isUpdate) {
                insertData(mapList, name, TM, Integer.parseInt(FPDR));
            }
            insertDataWater(mapList, latList.size(), lonList.size());
        }
    }

    private static List<Double> readArrayAsList(String dataStr, String fullName) {
        System.out.println("dataStr:::::::::::" + dataStr.length());
        // 使用正则表达式分割字符串，匹配一个或多个空白字符
        // String[] parts = dataStr.trim().split("\\s+");
        String[] parts = StringUtils.split(dataStr.trim(), ' ');
        System.out.println("parts:::::::::::" + parts.length);
        // 或者使用 Java 8 Stream API 来处理
        List<Double> dataListStream = IntStream.range(0, parts.length)
                .mapToObj(i -> Double.parseDouble(parts[i]))
                .collect(Collectors.toList());
        System.out.println(fullName + ":::::::::::" + dataListStream.size());
        return dataListStream;
    }

    private List<Double> readArrayAsListStringTokenizer(String dataStr, String fullName) {
        System.out.println("readArrayAsListStringTokenizer(dataStr):::::::::::" + dataStr.length());
        writeLogTxtStr("方法readArrayAsListStringTokenizer(" + fullName + "的dataStr):::::::::::" + dataStr.length(),
                "FQWater.txt");
        List<Double> dataListStream = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(dataStr);
        while (tokenizer.hasMoreTokens()) {
            dataListStream.add(Double.parseDouble(tokenizer.nextToken()));
        }
        System.out.println("dataListStream:::::::::::" + dataListStream.size());
        writeLogTxtStr(fullName + "的dataListStream:::::::::::" + dataListStream.size(), "FQWater.txt");
        return dataListStream;
    }

    /**
     * 直接从UCAR Array读取为double[]，避免toString+StringTokenizer的巨大开销
     */
    private double[] toDoubleArray(Array data) {
        Class<?> elementType = data.getElementType();
        if (elementType == double.class) {
            return (double[]) data.copyTo1DJavaArray();
        } else if (elementType == float.class) {
            float[] fa = (float[]) data.copyTo1DJavaArray();
            double[] da = new double[fa.length];
            for (int i = 0; i < fa.length; i++) {
                da[i] = fa[i];
            }
            return da;
        } else {
            // fallback: 逐元素getDouble
            long size = data.getSize();
            double[] da = new double[(int) size];
            for (int i = 0; i < size; i++) {
                da[i] = data.getDouble(i);
            }
            return da;
        }
    }

    public void FQ6HourWater() throws IOException {
        String filePath = filePathName + "fq/FQ6Hour";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        // LocalDateTime dateTime =
        // LocalDateTime.of(now.getYear(),now.getMonth(),1,0,0,0).toLocalDate().atStartOfDay();
        // long ms = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        LocalDateTime dateTime = now.minusHours(3);
        Date dateTimedate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        downGRB(filePath, FtpIP, FtpPort, "swzz_6h", "zxt@18SH", dateTimedate);// 202.96.202.173
        File file = new File(filePath);
        File[] files = file.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (null != files) {
            fileNameList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }
        for (String name : fileNameList) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            StringBuilder sbd = new StringBuilder();
            int startIndex = name.indexOf("Z_GRID_RAIN_");
            String tmStr = name.substring(startIndex + 12, startIndex + 12 + 12);
            Date tmDate = null;
            try {
                tmDate = df.parse(tmStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date threeDayTm = new Date(tmDate.getTime() - 24 * 60 * 60 * 1000);
            int index = name.indexOf("SPCC_");
            String tmStrT = name.substring(index + 5, index + 5 + 12);
            Date YBTM = null;
            try {
                YBTM = df.parse(tmStrT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("==========开始解析:" + name + "=================");
            sbd.append("==========开始解析:").append(name).append("=================\n");
            if (tmDate.getTime() >= threeDayTm.getTime()) {
                File openFile = new File(filePath + File.separatorChar + name);
                InputStream in = new FileInputStream(openFile);

                long pt = 0;
                long filelen = in.available();// 流长度：9437232
                float sLgtd = 0;// 起始经度：115.7465
                float sLgtdVal = 0;// 经度间隔：0.03098
                float xCount = 0;// x方向（经向）格点数：256
                float sLttd = 0;// 起始纬度：27.55755
                float sLttdVal = 0;// 纬度间隔：0.026963
                float yCount = 0;// y方向（纬向）格点数：256
                float startT = 0;// 起始时次:10分钟

                StringBuilder GridRain = new StringBuilder();
                List<Float> list = new ArrayList<>();
                while (in.available() > 0) { // 检查文件中是否还有可读数据
                    byte[] bytes = new byte[4]; // 创建4字节的字节数组
                    in.read(bytes, 0, 4); // 读取4字节数据
                    ByteBuffer buffer = null;
                    boolean isBigEnd = isBigEndian(bytes);// 大端or小端模式，true是大端模式，false是小端模式，需要返序
                    // buffer=ByteBuffer.wrap(bytes);//BIG_ENDIAN大端模式
                    // if(!isBigEnd){
                    buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);// LITTLE_ENDIAN小端模式，BIG_ENDIAN大端模式
                    // }
                    if (pt == 0) {
                        sLgtd = buffer.getFloat();
                    }
                    if (pt == 4) {
                        sLgtdVal = buffer.getFloat();
                    }
                    if (pt == 8) {
                        xCount = buffer.getFloat();
                    }
                    if (pt == 12) {
                        sLttd = buffer.getFloat();
                    }
                    if (pt == 16) {
                        sLttdVal = buffer.getFloat();
                    }
                    if (pt == 20) {
                        yCount = buffer.getFloat();
                        // System.out.println("读取的yCount值: " + yCount+",isBigEnd："+isBigEnd+",pt："+pt);
                        // // 输出结果
                    }
                    if (pt == 24) {
                        startT = buffer.getFloat();
                    }
                    // br.read();//终止时次:360分钟
                    // br.read();//间隔时次:10分钟一个数
                    // br.read(); //预留1
                    // br.read();//预留2
                    // br.read();//预留3
                    if (pt > 44) {// 下面是雨量值
                        float lineStr = buffer.getFloat();
                        list.add(lineStr);
                    }

                    pt = pt + 4;// 每次读四个字节
                }
                // System.out.println("起始经度sLgtd: " +
                // sLgtd+",经度间隔sLgtdVal："+sLgtdVal+",x方向（经向）格点数xCount："+xCount); // 输出结果
                // System.out.println("起始纬度sLttd: " +
                // sLttd+",纬度间隔sLttdVal："+sLttdVal+",y方向（纬向）格点数yCount："+yCount);
                // System.out.println("起始时次startT: " + startT);
                // System.out.println("雨量数组长度: " + list.size());
                // long byteTotal = Files.size(Paths.get(filePath + File.separatorChar + name));
                // List<String> strAll = new ArrayList<>();
                // System.out.println("byteLen="+byteTotal);
                int dataLength = (int) (xCount * yCount);
                int dataCount = list.size() / dataLength;
                int hours = dataCount / 6;
                int _hour = 0;
                for (int hou = 0; hou < hours; hou++) {
                    List<Map<String, Object>> maps = new ArrayList<>();
                    for (int i = 0; i < hours; i++) {
                        int _index = 0;
                        for (int j = (hou + _hour) * dataLength; j < (hou + _hour + 1) * dataLength; j++) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("gridCode", _index);
                            map.put("drp", list.get(j));
                            maps.add(map);
                            _index++;
                        }
                    }
                    _hour = _hour + hours;
                    Map<String, List<Map<String, Object>>> listMap = maps.stream()
                            .collect(Collectors.groupingBy(m -> m.get("gridCode").toString()));
                    Set<String> keySet = listMap.keySet();
                    List<String> keyList = keySet.stream().sorted().collect(Collectors.toList());
                    List<Double> drpList = new ArrayList<>();
                    for (String key : keyList) {
                        List<Map<String, Object>> mapsT = listMap.get(key);
                        double drpSum = mapsT.stream().mapToDouble(m -> Double.parseDouble(m.get("drp").toString()))
                                .sum();
                        drpList.add(drpSum);
                    }
                    Map<String, Object> map = new HashMap<>();
                    String FTM = dateFormat.format(new Date(YBTM.getTime() + (hou + 1) * 60 * 60 * 1000));
                    map.put("hour", hou);
                    map.put("FTM", FTM);// 时间
                    map.put("RLSTM", dateFormat.format(YBTM));// 预报时间
                    map.put("data", drpList);
                    map.put("FPDR", 6);
                    map.put("HOURS", 1);
                    mapList.add(map);
                }
                in.close();
            }
            // String s = sbd.toString();
            // byte[] byteArr = s.getBytes(StandardCharsets.UTF_8);
            // FileOutputStream out = new FileOutputStream(new
            // File("D:\\work\\UploadDoc\\log\\6hour\\" + name + "_log.txt"));
            // out.write(byteArr);
            // out.close();
            insertData(mapList, name, dateFormat.format(YBTM), 6);
            insertData6HourWater(mapList);
        }
    }

    public void FQ6HourWaterGong() throws IOException {
        writeLogTxtStr("==========开始执行FQ6HourWaterGong=================", "FQ6HourWaterGong.txt");
        String filePath = filePathName + "fq/FQ6Hour";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        // LocalDateTime dateTime
        // =LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),now.getHour()-3,0,0).toLocalDate().atStartOfDay();
        // long ms = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        LocalDateTime dateTime = now.minusHours(3);
        Date dateTimedate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        downGRB(filePath, FtpIP, FtpPort, "swzz_6h", "zxt@18SH", dateTimedate);// 202.96.202.173
        File file = new File(filePath);
        File[] files = file.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (null != files) {
            fileNameList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }
        for (String name : fileNameList) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            StringBuilder sbd = new StringBuilder();
            int startIndex = name.indexOf("Z_GRID_RAIN_");
            String tmStr = name.substring(startIndex + 12, startIndex + 12 + 12);
            Date tmDate = null;
            try {
                tmDate = df.parse(tmStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date threeDayTm = new Date(tmDate.getTime() - 24 * 60 * 60 * 1000);
            int index = name.indexOf("SPCC_");
            String tmStrT = name.substring(index + 5, index + 5 + 12);
            Date YBTM = null;
            try {
                YBTM = df.parse(tmStrT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<String> typeList = Arrays.asList("6");
            List<Tz_ncfilePojo> tzNcfileList = ncfileData.selectList(null, null, tmStrT, tmStrT, typeList, null, null);
            if (tzNcfileList.size() > 0) {// 已经存库了
                continue;
            }
            System.out.println("==========开始解析:" + name + "=================");
            sbd.append("==========开始解析:").append(name).append("=================\n");
            writeLogTxtStr("==========开始解析：" + name + "=================", "FQ6HourWaterGong.txt");
            if (tmDate.getTime() >= threeDayTm.getTime()) {
                String path = filePath + File.separatorChar + name;
                FileInputStream fis = null;
                DataInputStream dis = null;
                try {
                    StringBuilder gridRain = new StringBuilder();
                    List<Float> list = new ArrayList<>();
                    fis = new FileInputStream(path);
                    dis = new DataInputStream(fis);

                    long filelen = fis.getChannel().size(); // 流长度
                    writeLogTxtStr("==========流长度：" + filelen + "=================", "FQ6HourWaterGong.txt");
                    System.out.println("==========流长度：" + filelen + "=================");
                    // if(filelen!=9437232){//流长度不对
                    // continue;
                    // }
                    long pt = 1;
                    // 读取起始经度
                    float sLgtd = readFloat(dis);
                    System.out.println("起始经度: " + sLgtd);

                    // 读取经度间隔
                    float sLgtdVal = readFloat(dis);
                    System.out.println("经度间隔: " + sLgtdVal);

                    // 读取x方向格点数
                    int xCount = (int) readFloat(dis);
                    System.out.println("x方向格点数: " + xCount);

                    // 读取起始纬度
                    float sLttd = readFloat(dis);
                    System.out.println("起始纬度: " + sLttd);

                    // 读取纬度间隔
                    float sLttdVal = readFloat(dis);
                    System.out.println("纬度间隔: " + sLttdVal);

                    // 读取y方向格点数
                    int yCount = (int) readFloat(dis);
                    System.out.println("y方向格点数: " + yCount);

                    // 读取起始时次
                    int startT = (int) readFloat(dis);
                    System.out.println("起始时次: " + startT);

                    // 读取终止时次
                    float endT = readFloat(dis);
                    System.out.println("终止时次: " + endT);

                    // 读取间隔时次
                    float intervalT = readFloat(dis);
                    System.out.println("间隔时次: " + intervalT);

                    // 读取预留字段
                    float reserved1 = readFloat(dis);
                    System.out.println("预留1: " + reserved1);

                    float reserved2 = readFloat(dis);
                    System.out.println("预留2: " + reserved2);

                    float reserved3 = readFloat(dis);
                    System.out.println("预留3: " + reserved3);

                    pt = fis.getChannel().position();
                    while (pt < filelen) {
                        float lineStr = readFloat(dis); // 一次读取四个字节
                        gridRain.append(lineStr).append("|");
                        list.add(lineStr);
                        pt = fis.getChannel().position();
                    }
                    String message = gridRain.toString().substring(0, gridRain.toString().length() - 1);
                    // LOG.log(message, "");
                    // writeLogTxtStr("FQ6HourWaterGong解析的预报雨量内容："+message,"FQ6HourWaterGong.txt");
                    String[] messageArr = message.split("\\|");
                    int dataLength = xCount * yCount;
                    float[] valArr = new float[dataLength];
                    int dataCount = messageArr.length / dataLength; // 多少个10分钟
                    int hours = dataCount / 6; // 多少个小时

                    List<Map<String, Object>> dicValueNew = new ArrayList<>();
                    int hour = 0;
                    for (int val = 0; val < hours; val++) { // 10分钟一个数据
                        List<GridRain36010Pojo> listData = new ArrayList<>();
                        for (int h = 0; h < hours; h++) { // 处理一个小时一个数据
                            int indexGrid = 0;
                            for (int num = (val + hour) * dataLength; num < (val + hour + 1) * dataLength; num++) {
                                GridRain36010Pojo dto = new GridRain36010Pojo();
                                dto.setGridCode(indexGrid);
                                dto.setDrp(list.get(num));
                                listData.add(dto);
                                indexGrid++;
                            }
                        }
                        hour += hours;
                        Map<Integer, Float> gridMap = new HashMap<>();
                        for (GridRain36010Pojo dto : listData) {
                            gridMap.merge(dto.getGridCode(), dto.getDrp(), Float::sum);
                        }
                        List<Float> listNew = new ArrayList<>(gridMap.values());
                        Map<String, Object> dicValue = new HashMap<>();
                        String ftm = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                        String FTM = dateFormat.format(new Date(YBTM.getTime() + (val + 1) * 60 * 60 * 1000));
                        dicValue.put("hour", val);
                        dicValue.put("FTM", FTM); // 时间
                        dicValue.put("RLSTM", dateFormat.format(YBTM)); // 预报时间
                        dicValue.put("data", listNew);
                        dicValue.put("FPDR", 6);
                        dicValue.put("HOURS", 1);
                        dicValueNew.add(dicValue);
                    }

                    mapList = dicValueNew;
                } catch (Exception ex) {
                    // ex.printStackTrace();
                    writeLogTxtStr("执行readGridRain36010报错：" + ex.getMessage(), "FQ6HourWaterGong.txt");
                } finally {
                    try {
                        if (dis != null)
                            dis.close();
                        if (fis != null)
                            fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // String s = sbd.toString();
            // byte[] byteArr = s.getBytes(StandardCharsets.UTF_8);
            // FileOutputStream out = new FileOutputStream(new
            // File("D:\\work\\UploadDoc\\log\\6hour\\" + name + "_log.txt"));
            // out.write(byteArr);
            // out.close();
            insertData(mapList, name, dateFormat.format(YBTM), 6);
            insertData6HourWater(mapList);
        }
    }

    public void FQ6HourWaterGong6() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        writeLogTxtStr("==========开始执行FQ6HourWaterGong=================",
                "FQ6HourWaterGong" + formattedDateLog + ".txt");
        String filePath = filePathName + "fq/FQ6Hour";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.minusHours(3);
        Date dateTimedate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("6小时的开始抓取时间为：" + dateTime);
        writeLogTxtStr("==========6小时的开始抓取时间为=================" + dateTime,
                "FQ6HourWaterGong" + formattedDateLog + ".txt");
        long ms = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        downGRB6(filePath, FtpIP, FtpPort, "swzz_6h", "zxt@18SH", "/", dateTimedate);// 202.96.202.173
        File file = new File(filePath);
        File[] files = file.listFiles();
        List<String> fileNameList = new ArrayList<>();
        if (null != files) {
            fileNameList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }
        writeLogTxtStr("==========本次下载的6h文件数量" + files.length + "=================",
                "FQ6HourWaterGong" + formattedDateLog + ".txt");
        for (String name : fileNameList) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            StringBuilder sbd = new StringBuilder();
            int startIndex = name.indexOf("Z_GRID_RAIN_");
            String tmStr = name.substring(startIndex + 12, startIndex + 12 + 12);
            Date tmDate = null;
            try {
                tmDate = df.parse(tmStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date threeDayTm = new Date(tmDate.getTime() - 24 * 60 * 60 * 1000);
            int index = name.indexOf("SPCC_");
            String tmStrT = name.substring(index + 5, index + 5 + 12);
            Date YBTM = null;
            try {
                YBTM = df.parse(tmStrT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (YBTM.getTime() < dateTimedate.getTime()) {
                continue;
            }
            List<String> typeList = Arrays.asList("6");
            List<Tz_ncfilePojo> tzNcfileList = ncfileData.selectList(null, null, tmStrT, tmStrT, typeList, null, null);
            if (tzNcfileList.size() > 0) {// 已经存库了
                continue;
            }
            System.out.println("==========开始解析:" + name + "=================");
            sbd.append("==========开始解析:").append(name).append("=================\n");
            writeLogTxtStr("==========开始解析：" + name + "=================",
                    "FQ6HourWaterGong" + formattedDateLog + ".txt");
            List<SDE_AREA> listArea = new ArrayList<>();
            try {
                if (tmDate.getTime() >= threeDayTm.getTime()) {
                    String path = filePath + File.separatorChar + name;
                    listArea = GridRainReader.readGridRain36010New(path, dateFormat.format(YBTM), 6, 1, filePathName);
                }
            } catch (Exception ex) {
                // ex.printStackTrace();
                writeLogTxtStr("执行readGridRain36010报错：" + ex.getMessage(),
                        "FQ6HourWaterGong" + formattedDateLog + ".txt");
            }
            if (!listArea.isEmpty()) {
                insertDataWater6HourNew(listArea, dateFormat.format(YBTM), 6);
                insertData(mapList, name, dateFormat.format(YBTM), 6);
            }
        }
    }

    public void FQ6HourWaterGong6Test() throws IOException {
        // 测试下载文件**********************************************************************开始
        String filePath = filePathName + "fq/FQ6Hour";
        FtpFolderDownloader downloader = new FtpFolderDownloader(
                FtpIP, FtpPort, "swzz_6h", "zxt@18SH",
                "", filePath);
        downloader.startMonitoring(300);
        try {
            downloader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 测试下载文件**********************************************************************结束
    }

    public float readFloat(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[4];
        dis.readFully(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置为小端字节序
        return buffer.getFloat();
    }

    public static boolean isBigEndian(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        // 检查第一个byte是否为0，如果是，则是大端模式
        return buffer.get() == 0;
    }

    public void insertDataWater(List<Map<String, Object>> mapList, int latCount, int lonCount) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<String> typeList = Arrays.asList("48&336");
        List<Tz_watershedwgPojo> listGrid = tz_watershedwgData.selectList(null, null, null, null, typeList, null, null);

        List<Tz_watershedPojo> tzWatershedList = tzWatershedData.selectList(null, null, null, null, null, null, null);
        List<Tz_watersheddataPojo> insertList = new ArrayList<>();
        List<Tz_watersheddataPojo> updateList = new ArrayList<>();
        // 查询该起报时间下已有的流域数据（只查一次，所有时次共用）
        String rlstm = mapList.get(0).get("RLSTM").toString();
        String fpdrStr = mapList.get(0).get("FPDR").toString();
        List<String> fpdrTypes = Collections.singletonList(String.valueOf(Integer.parseInt(fpdrStr)));
        List<Tz_watersheddataPojo> allList = tzWatersheddataData.selectByTimeAndFPDR(
                null, null, rlstm, rlstm, fpdrTypes);
        writeLogTxtStr("insertDataWater: allList.size=" + allList.size() + " rlstm=" + rlstm + " fpdr=" + fpdrStr,
                "FQWater.txt");
        writeLogTxtStr("insertDataWater: listGrid.size=" + listGrid.size() + " tzWatershedList.size=" + tzWatershedList.size(),
                "FQWater.txt");
        if (listGrid.isEmpty() || tzWatershedList.isEmpty()) {
            writeLogTxtStr("insertDataWater: ABORT listGrid或tzWatershedList为空，跳过入库", "FQWater.txt");
            return;
        }
        for (Map<String, Object> map : mapList) {
            try {
            List<SDE_AREAPojo> sdeAreaList = new ArrayList<>(); // sdeAreaData.selectList(null, null, null);
            double[] dataArray = (double[]) map.get("dataArray");
            int sliceOffset = (int) map.get("sliceOffset");
            // for (SDE_AREAPojo sde : sdeAreaList){
            // Integer fid = sde.getFID();
            // double z1 = Math.abs((56 - (fid + 1) / 51) * 51);
            // double z2 = (fid + 1) % 51;
            // int wg = (int) (z1 + z2);
            // sde.setZVALUE(data.get(wg));
            // }

            for (int num = 0; num < listGrid.size(); num++) {
                int fid = listGrid.get(num).getFID();
                SDE_AREAPojo dto = new SDE_AREAPojo();
                dto.setFID(fid);
                dto.setAREANAME(listGrid.get(num).getNAME());
                // FID方向是北→南(row=0→35°N)，NetCDF data方向是南→北(idx=0→26°N)，需要Y轴翻转
                int row = fid / lonCount;
                int col = fid % lonCount;
                int realIdx = (latCount - 1 - row) * lonCount + col;
                dto.setZVALUE(dataArray[sliceOffset + realIdx]);
                sdeAreaList.add(dto);
            }

            for (Tz_watershedPojo watershed : tzWatershedList) {
                List<SDE_AREAPojo> listAreaTemp = sdeAreaList.stream().filter(m -> {
                    if (null != m.getAREANAME() && null != watershed.getNAME()) {
                        return m.getAREANAME().equals(watershed.getNAME());
                    }
                    return false;
                }).collect(Collectors.toList());
                double DRP = 0.0;
                if (listAreaTemp.size() > 0) {
                    DRP = listAreaTemp.stream().mapToDouble(m -> {
                        if (null != m.getZVALUE() && !String.valueOf(m.getZVALUE()).isEmpty()
                                && String.valueOf(m.getZVALUE()).matches("-?\\d+(\\.\\d+)?")) {
                            return m.getZVALUE();
                        }
                        return 0.0;
                    }).sum() / listAreaTemp.size();
                }

                // 使用Stream API计算ZVALUE的平均值
                // DRP = listAreaTemp.stream()
                // .mapToDouble(SDE_AREAPojo::getZVALUE)
                // .average()
                // .orElse(Double.NaN); // 如果列表为空，则返回NaN

                int hour = Integer.parseInt(map.get("HOURS").toString());
                DRP = DRP / hour;// List l = new M();
                for (int i = 0; i < hour; i++) {
                    Tz_watersheddataPojo obj = new Tz_watersheddataPojo();
                    obj.setKEYID(watershed.getKEYID());
                    String ftm = map.get("FTM").toString();
                    String FTM = null;
                    try {
                        FTM = dateFormat.format(new Date(dateFormat.parse(ftm).getTime() + (i * 60 * 60 * 1000)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (watershed.getKEYID().equals("1744830552")) {
                        System.out.println(watershed.getNAME() + "," + FTM + ",DRP(" + DRP + "),listAreaTemp.size("
                                + listAreaTemp.size() + ")");
                    }
                    obj.setFTM(FTM);
                    obj.setRLSTM(map.get("RLSTM").toString());
                    obj.setFPDR(Double.valueOf(map.get("FPDR").toString()));
                    obj.setDRP(new BigDecimal(Double.toString(DRP)).setScale(1, RoundingMode.HALF_UP).doubleValue());// (double)
                                                                                                                     // Math.round(DRP
                                                                                                                     // *
                                                                                                                     // 10.0)
                                                                                                                     // /
                                                                                                                     // 10.0
                    obj.setTYPE("上海气象台");
                    // System.out.println("================="+map.get("FDRP")+"=================");
                    if (!insertList.contains(obj) && !updateList.contains(obj)) {
                        if (allList.contains(obj)) {
                            updateList.add(obj); // 已存在，更新DRP
                        } else {
                            insertList.add(obj); // 新记录，插入
                        }
                    }
                }
            }
            } catch (Exception e) {
                writeLogTxtStr("insertDataWater: ERROR in time step: " + e.toString(), "FQWater.txt");
                e.printStackTrace();
            }
        }
        // 更新已存在的记录（MyBatis Batch模式，所有updateOne攒成一个JDBC batch，一次网络往返）
        writeLogTxtStr("insertDataWater: updateList=" + updateList.size() + " insertList=" + insertList.size(),
                "FQWater.txt");
        if (updateList.size() > 0) {
            org.apache.ibatis.session.SqlSession batchSession = swzzqxsjSqlSessionFactory
                    .openSession(org.apache.ibatis.session.ExecutorType.BATCH);
            try {
                Tz_watersheddataData batchMapper = batchSession.getMapper(Tz_watersheddataData.class);
                for (Tz_watersheddataPojo pojo : updateList) {
                    batchMapper.updateOne(pojo);
                }
                batchSession.flushStatements();
                batchSession.commit();
            } finally {
                batchSession.close();
            }
            writeLogTxtStr("insertDataWater: updateAll done, total=" + updateList.size(), "FQWater.txt");
        }
        // 插入新记录
        if (insertList.size() > 0) {
            int count = 4000;
            int num = insertList.size() / count;
            if (insertList.size() % count != 0) {
                num += 1;
            }
            List<Tz_watersheddataPojo> subList = null;
            for (int i = 0; i < num; i++) {
                if (i == num - 1) {
                    subList = insertList.subList(i * count, insertList.size());
                } else {
                    subList = insertList.subList(i * count, (i + 1) * count);
                }
                tzWatersheddataData.insertALL(subList);
            }
        }
    }

    public void insertData6HourWater(List<Map<String, Object>> mapList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<SDE_AREA6HOURPojo> sdeAreaList = sdeArea6HOURData.selectList(null, null, null);
        List<Tz_watershedPojo> tzWatershedList = tzWatershedData.selectList(null, null, null, null, null, null, null);
        List<Tz_watersheddataPojo> list = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            List<String> typeList = Arrays.asList("6");
            List<Tz_watersheddataPojo> allList = tzWatersheddataData.selectList(null, null, map.get("RLSTM").toString(),
                    map.get("RLSTM").toString(), typeList, null, null);
            List<Double> data = (List<Double>) map.get("data");
            for (SDE_AREA6HOURPojo sde : sdeAreaList) {
                Integer fid = sde.getFID();
                double z1 = Math.abs((255 - (fid + 1) / 256) * 256);
                double z2 = (fid + 1) % 256;
                int wg = (int) (z1 + z2);

                try {
                    sde.setZVALUE(data.get(wg));
                } catch (Exception ex) {
                    Number number = (Number) data.get(wg);
                    Double value = number.doubleValue();
                    sde.setZVALUE(value);
                }
            }
            for (Tz_watershedPojo watershed : tzWatershedList) {
                List<SDE_AREA6HOURPojo> listAreaTemp = sdeAreaList.stream().filter(m -> {
                    if (null != m.getAREANAME()) {
                        return m.getAREANAME().equals(watershed.getNAME());
                    }
                    return false;
                }).collect(Collectors.toList());
                double DRP = 0.0;
                if (listAreaTemp.size() > 0) {
                    DRP = listAreaTemp.stream().mapToDouble(m -> m.getZVALUE()).sum() / listAreaTemp.size();
                }
                int hour = Integer.parseInt(map.get("HOURS").toString());
                DRP = DRP / hour;
                for (int i = 0; i < hour; i++) {
                    Tz_watersheddataPojo obj = new Tz_watersheddataPojo();
                    obj.setKEYID(watershed.getKEYID());
                    String ftm = map.get("FTM").toString();
                    String FTM = null;
                    try {
                        FTM = dateFormat.format(new Date(dateFormat.parse(ftm).getTime() + ((i + 1) * 60 * 60 * 1000)));
                        writeLogTxtStr(
                                "=====FQ6HourWaterGong=====发布时间RLSTM:" + map.get("RLSTM").toString() + ",时间FTM:" + FTM,
                                "FQ6HourWaterGong.txt");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    double drp = Math.round(DRP * 10) / 10.0;
                    obj.setFTM(FTM);
                    obj.setRLSTM(map.get("RLSTM").toString());
                    obj.setFPDR(Double.valueOf(map.get("FPDR").toString()));
                    DecimalFormat format = new DecimalFormat("#.0");
                    obj.setDRP(Double.isNaN(drp) ? 0.0 : Double.parseDouble(format.format(drp)));
                    obj.setTYPE("上海气象台");
                    if (!list.contains(obj) && !allList.contains(obj)) {
                        list.add(obj);
                    }
                }
            }
        }
        if (list.size() > 0) {
            int count = 4000;
            int num = list.size() / count;
            if (list.size() % count != 0) {
                num += 1;
            }
            List<Tz_watersheddataPojo> subList = null;
            for (int i = 0; i < num; i++) {
                if (i == num - 1) {
                    subList = list.subList(i * count, list.size());
                } else {
                    subList = list.subList(i * count, (i + 1) * count);
                }
                tzWatersheddataData.insertALL(subList);
            }
        }
    }

    public void insertDataWater6HourNew(List<SDE_AREA> list, String TM, int hourlySteps) {
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);

            writeLogTxtStr("==========进入insertDataWater6HourNew=================",
                    "FQ6HourWaterGong" + formattedDateLog + ".txt");

            List<SDE_AREA6HOURPojo> listArea = sdeArea6HOURData.selectList(null, null, null);
            writeLogTxtStr("==========insertDataWater6HourNew区域数量：" + listArea.size() + "=================",
                    "FQ6HourWaterGong" + formattedDateLog + ".txt");

            List<Tz_watershedPojo> listSHED = tzWatershedData.selectList(null, null, null, null, null, null, null);

            writeLogTxtStr("==========insertDataWater6HourNew网格数量：" + listSHED.size() + "=================",
                    "FQ6HourWaterGong" + formattedDateLog + ".txt");

            List<Tz_watersheddataPojo> listData = new ArrayList<>();
            StringBuilder sbFID = new StringBuilder();
            for (int h = 0; h < hourlySteps; h++)// 多少个小时的数据
            {
                int finalH = h;
                List<SDE_AREA> listTemp = list.stream().filter(p -> p.getCOLOR() == finalH)
                        .collect(Collectors.toList());
                for (int num = 0; num < listSHED.size(); num++) {
                    int finalNum = num;
                    List<SDE_AREA6HOURPojo> listAreaTemp = listArea.stream()
                            .filter(u -> u.getAREANAME().equals(listSHED.get(finalNum).getNAME()))
                            .collect(Collectors.toList());
                    String[] fidList = listAreaTemp.stream()
                            .map(p -> String.valueOf(p.getFID()))
                            .toArray(String[]::new);
                    List<SDE_AREA> listTempT = listTemp.stream()
                            .filter(p -> Arrays.asList(fidList).contains(String.valueOf(p.getFID())))
                            .collect(Collectors.toList());

                    if (h == 0) {
                        sbFID.append("{\"NAME\":" + listSHED.get(num).getNAME() + ",\"fidList\":"
                                + String.join(",", fidList) + "}");
                    }

                    float DRP = 0;
                    if (listAreaTemp.size() > 0 && listTempT.size() > 0) {
                        float totalDrp = (float) listTempT.stream()
                                .mapToDouble(u -> u.getZVALUE())
                                .sum();
                        DRP = totalDrp / fidList.length;
                    }
                    Tz_watersheddataPojo dto = new Tz_watersheddataPojo();
                    LocalDateTime FTMNew = LocalDateTime.parse(TM, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            .plusHours(h + 1);// 记录结束时间
                    String formattedDateTime = FTMNew.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    dto.setKEYID(listSHED.get(num).getKEYID());
                    dto.setFTM(formattedDateTime);
                    dto.setRLSTM(TM);
                    dto.setFPDR(6.0);
                    dto.setDRP(Math.round(DRP * 10.0) / 10.0);
                    dto.setTYPE("上海气象台");
                    listData.add(dto);
                }
            }
            writeLogTxtStr("==========6小时的数据总量为：" + listData.size() + "=================",
                    "FQ6HourWaterGong" + formattedDateLog + ".txt");
            if (listData.size() > 0) {
                tzWatersheddataData.insertALL(listData);
            }
        } catch (Exception ex) {
        }
    }

    public void insertData(List<Map<String, Object>> mapList, String ncpath, String TM, int FPDR) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Tz_ncfilePojo> tzNcfileList = ncfileData.selectList(null, null, TM, TM,
                Collections.singletonList(String.valueOf(FPDR)), null, null);
        int index = tzNcfileList.size();
        if (index == 0) {
            List<Tz_ncfilelistPojo> list = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                List<Double> newDataList = new ArrayList<>();
                List<Double> data = map.get("data") instanceof List ? (List<Double>) map.get("data") : null;
                String FTM = map.get("FTM").toString();
                String RLSTM = map.get("RLSTM").toString();
                int HOUR = Integer.parseInt(map.get("HOURS").toString());
                if (null != data) {
                    // for (Double num : data){
                    // newDataList.add(Double.parseDouble(String.format("%.1f",num / HOUR))) ;
                    // }
                    for (int i = 0; i < HOUR; i++) {
                        String FTMNew = null;
                        try {
                            FTMNew = dateFormat
                                    .format(new Date(dateFormat.parse(FTM).getTime() + (i * 60 * 60 * 1000)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // if (FPDR == 6){
                        // photoPICSix(newDataList, RLSTM, FTMNew, FPDR);
                        // }else {
                        // photoPIC(newDataList, RLSTM, FTMNew, FPDR);
                        // }
                        Tz_ncfilelistPojo dto = new Tz_ncfilelistPojo();
                        dto.setID(UUID.randomUUID().toString().replaceAll(" ", "-"));
                        dto.setTM(FTMNew);
                        dto.setRLSTM(RLSTM);
                        dto.setFPDR((double) FPDR);
                        list.add(dto);
                    }
                }
            }
            if (list.size() > 0) {
                Integer num = ncfilelistData.insertAll(list);
                if (num > 0) {

                }
            }

            Tz_ncfilePojo pojo = new Tz_ncfilePojo();
            pojo.setID(UUID.randomUUID().toString().replaceAll(" ", "-"));
            pojo.setFPDR((double) FPDR);
            pojo.setNCFILE(ncpath);
            pojo.setTM(TM);
            ncfileData.insertOne(pojo);
        }
    }

    private void photoPIC(List<Double> newDataList, String rlstm, String ftmNew, int fpdr) {
    }

    private void photoPICSix(List<Double> newDataList, String rlstm, String ftmNew, int fpdr) {
    }

    private void downGRB(String filePath, String host, int port, String username, String pass, Date ms) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        FTPClient ftpClient = null;
        StringBuilder builder = new StringBuilder();
        try {
            Process exec = Runtime.getRuntime().exec("telnet " + host + " " + port);
            InputStream input = exec.getInputStream();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(input));
            String msg = "";
            builder.append("telnet检测ip和端口:").append("\n");
            while ((msg = bufReader.readLine()) != null) {
                builder.append(msg);
            }
            builder.append("q").append("\n");
            ftpClient = MyFtpClient.open(host, port, username, pass);
            ftpClient.changeWorkingDirectory("/");
            builder.append("b").append("\n");
            // String[] ftpFilesnme = ftpClient.listNames();
            // builder.append("a").append("\n");
            FTPFile[] ftpFiles = ftpClient.listFiles();
            builder.append("a").append("\n");
            List<String> names = Arrays.stream(ftpFiles).map(FTPFile::getName).collect(Collectors.toList());
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            System.out.println("================开始下载grb文件=================");
            for (FTPFile ftpFile : ftpFiles) {
                String fileName = ftpFile.getName();
                Calendar timestamp = ftpFile.getTimestamp();// 这是FTPFile类的方法，用于获取该文件在FTP服务器上的最后修改时间

                long timeInMillis = timestamp.getTimeInMillis();
                if (timestamp.getTime().getTime() < ms.getTime()) {
                    continue;
                }
                System.out.println(fileName + "最后修改时间为" + timestamp.getTime().getTime() + "，限制时间为：" + ms.getTime());
                File file = null;
                file = new File(filePath + "/" + fileName);
                builder.append(filePath).append("/").append(fileName).append("\n");
                if (file.exists()) {
                    continue;
                }
                if (fileName.contains("GRB2") && !(fileName.contains("P_RFFC_SPCC-ER01-CSJ_")
                        || fileName.contains("P_RFFC_SPCC-ER03-CSJ_"))) {
                    continue;
                } else {
                    int strIndex = fileName.contains("P_RFFC_SPCC-ER01-CSJ_")
                            ? fileName.indexOf("P_RFFC_SPCC-ER01-CSJ_")
                            : fileName.indexOf("P_RFFC_SPCC-ER03-CSJ_");
                    String TM = fileName.substring(strIndex + 21, strIndex + 21 + 12);
                    Date tmDate = new Date();
                    try {
                        tmDate = df.parse(TM);
                    } catch (ParseException e) {
                    }
                    Date threeDayTm = new Date(tmDate.getTime() - 12 * 60 * 60 * 1000);
                    if (tmDate.getTime() < threeDayTm.getTime()) {// 12小时外的不同步
                        continue;
                    }
                }
                OutputStream fileOut = new FileOutputStream(file);
                boolean retrieveFile = ftpClient.retrieveFile(fileName, fileOut);
                fileOut.flush();
                fileOut.close();
                if (!retrieveFile) {
                    // 下载失败，删除不完整的文件，下次重新下载
                    System.out.println(fileName + " 下载失败，删除不完整文件");
                    file.delete();
                }
            }
            System.out.println("================下载grb文件结束，共" + names.size() + "个文件=================");
            MyFtpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("error:").append(e.getMessage()).append("\n");
        }
        try {
            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/GrbFileList.txt", true);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downGRB6(String filePath, String host, int port, String username, String pass, String remoteDir, Date ms) {
        FTPClient ftpClient = null;
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat logSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.append("=== [").append(logSdf.format(new Date())).append("] ").append(remoteDir).append(" ===\n");
        // 使用 try-with-resources 管理本地文件流，避免泄露
        // 注意：不能在循环外定义 out，必须每个文件独立管理

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        // 设置时区，避免时间解析偏差
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        try {
            builder.append("开始连接 FTP: ").append(host).append(":").append(port).append("\n");

            ftpClient = new FTPClient();
            // 关键配置：增加超时时间，防止大文件传输中途断开
            ftpClient.setConnectTimeout(60000);
            ftpClient.setDefaultTimeout(60000);
            ftpClient.setDataTimeout(300000);
            ftpClient.setControlEncoding("UTF-8"); // 根据服务器编码调整，有时需要 ISO-8859-1

            ftpClient.connect(host, port);

            // 检查连接状态
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("FTP 连接拒绝，响应码: " + reply);
            }

            boolean loginSuccess = ftpClient.login(username, pass);
            if (!loginSuccess) {
                throw new IOException("FTP 登录失败");
            }

            // ✅ 关键修复 1：强制开启被动模式 (解决防火墙和丢包问题)
            ftpClient.enterLocalPassiveMode();

            // ✅ 关键修复 2：设置二进制传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 设置缓冲区大小，提升大文件传输稳定性
            ftpClient.setBufferSize(1024 * 1024);

            ftpClient.changeWorkingDirectory(remoteDir);
            builder.append("成功切换目录 " + remoteDir + "，开始列出文件...\n");

            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles == null || ftpFiles.length == 0) {
                builder.append("警告：远程目录为空\n");
                return;
            }

            File localDir = new File(filePath);
            if (!localDir.exists()) {
                if (!localDir.mkdirs()) {
                    throw new IOException("无法创建本地目录: " + filePath);
                }
            }

            System.out.println("================开始下载 GRB 文件=================");
            builder.append("远程文件总数: ").append(ftpFiles.length).append("\n");

            int downloadCount = 0;
            int skipCount = 0;
            int failCount = 0;

            for (FTPFile ftpFile : ftpFiles) {
                if (!ftpFile.isFile())
                    continue;

                String fileName = ftpFile.getName();

                // 1. 时间过滤逻辑 (保持原有逻辑，增加空指针保护)
                Calendar timestamp = ftpFile.getTimestamp();
                if (timestamp == null) {
                    // 如果服务器没返回时间，尝试从文件名解析或跳过
                    builder.append("跳过文件 (无时间戳): ").append(fileName).append("\n");
                    continue;
                }

                long fileTimeMillis = timestamp.getTimeInMillis();
                if (fileTimeMillis < ms.getTime()) {
                    continue;
                }

                // 2. 文件名规则过滤 (保持原有逻辑)
                if (fileName.contains("GRB2") && !(fileName.contains("P_RFFC_SPCC-ER01-CSJ_")
                        || fileName.contains("P_RFFC_SPCC-ER03-CSJ_"))) {
                    continue;
                }

                // 3. 二次时间校验 (12小时限制)
                boolean isValidFile = false;
                if (!fileName.contains("GRB2")) {
                    isValidFile = true; // 非 GRB2 文件直接通过？根据原逻辑似乎是这样，或者原逻辑有遗漏
                } else {
                    int strIndex = -1;
                    if (fileName.contains("P_RFFC_SPCC-ER01-CSJ_")) {
                        strIndex = fileName.indexOf("P_RFFC_SPCC-ER01-CSJ_");
                    } else if (fileName.contains("P_RFFC_SPCC-ER03-CSJ_")) {
                        strIndex = fileName.indexOf("P_RFFC_SPCC-ER03-CSJ_");
                    }

                    if (strIndex != -1 && strIndex + 33 <= fileName.length()) {
                        String TM = fileName.substring(strIndex + 21, strIndex + 21 + 12);
                        try {
                            Date tmDate = df.parse(TM);
                            Date threeDayTm = new Date(tmDate.getTime() - 12 * 60 * 60 * 1000);
                            if (tmDate.getTime() >= threeDayTm.getTime()) {
                                isValidFile = true;
                            }
                        } catch (ParseException e) {
                            builder.append("时间解析失败: ").append(fileName).append("\n");
                        }
                    }
                }

                if (!isValidFile)
                    continue;

                // 4. 本地文件检查与坏文件清理
                File localFile = new File(filePath + "/" + fileName);
                builder.append("检查文件: ").append(fileName).append("\n");

                if (localFile.exists()) {
                    // ✅ 关键修复 3：如果文件存在但大小为 0 或明显过小，视为损坏，删除后重下
                    // 这里不判断具体大小，因为不同文件大小不同，但至少判断是否为 0
                    if (localFile.length() == 0) {
                        builder.append("发现空文件，删除后重试: ").append(fileName).append("\n");
                        if (!localFile.delete()) {
                            builder.append("删除空文件失败，跳过: ").append(fileName).append("\n");
                            continue;
                        }
                    } else {
                        // 文件存在且非空，跳过 (如果需要覆盖策略，可在此修改)
                        builder.append("文件已存在且非空，跳过: ").append(fileName).append("\n");
                        skipCount++;
                        continue;
                    }
                }

                // 5. 执行下载
                System.out.println("正在下载: " + fileName);
                builder.append("开始下载: ").append(fileName).append("\n");

                boolean success = false;
                // ✅ 关键修复 4：使用 try-with-resources 确保 OutputStream 绝对关闭
                try (OutputStream os = new FileOutputStream(localFile)) {
                    success = ftpClient.retrieveFile(fileName, os);

                    // ✅ 关键修复 5：retrieveFile 返回 true 仅代表流操作完成，必须确认 FTP 协议层面的成功
                    // 某些情况下 retrieveFile 返回 true 但服务器实际报错，需结合 completePendingCommand (虽然
                    // retrieveFile 内部通常调用了，但显式检查更稳)
                    // 对于 Apache Commons Net，retrieveFile 内部已经调用了 completePendingCommand，返回值即为最终结果。
                    // 但为了保险，我们可以再次检查回复码
                    if (success) {
                        int finalReply = ftpClient.getReplyCode();
                        if (!FTPReply.isPositiveCompletion(finalReply)) {
                            success = false;
                            builder.append("下载完成但服务器返回错误码: ").append(finalReply).append("\n");
                        }
                    }
                } catch (IOException e) {
                    builder.append("下载 IO 异常: ").append(e.getMessage()).append("\n");
                    // 发生异常时，删除可能产生的残缺文件
                    if (localFile.exists()) {
                        localFile.delete();
                    }
                    failCount++;
                    continue;
                }

                if (success) {
                    System.out.println("下载成功: " + fileName + " (" + localFile.length() + " bytes)");
                    builder.append("下载成功: ").append(fileName).append(", 大小: ").append(localFile.length()).append("\n");
                    downloadCount++;

                    // 可选：在这里直接调用 wgrib2 转换，避免后续逻辑依赖
                    // convertToNetcdf(localFile.getAbsolutePath());
                } else {
                    System.err.println("下载失败 (协议层): " + fileName);
                    builder.append("下载失败 (协议层): ").append(fileName).append("\n");
                    if (localFile.exists())
                        localFile.delete();
                    failCount++;
                }
            }

            System.out.println("================下载结束=================");
            System.out.println("成功: " + downloadCount + ", 跳过: " + skipCount + ", 失败: " + failCount);
            builder.append("统计 - 成功: ").append(downloadCount).append(", 跳过: ").append(skipCount).append(", 失败: ")
                    .append(failCount).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
            builder.append("致命错误: ").append(e.getMessage()).append("\n");
            builder.append("堆栈: ").append(getStackTraceAsString(e)).append("\n");
        } finally {
            // ✅ 关键修复 6：确保 FTP 连接在任何情况下都断开
            if (ftpClient != null) {
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                        builder.append("FTP 连接已关闭\n");
                    }
                } catch (IOException ioe) {
                    builder.append("关闭 FTP 连接时出错: ").append(ioe.getMessage()).append("\n");
                }
            }

            // 写入日志
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            writeLogToFile(builder.toString(), filePathName + "/logs/FQ6HourWaterGong" + formattedDateLog + ".txt");
        }
    }

    // 辅助方法：获取堆栈信息
    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    // 辅助方法：写日志 (简化版，请替换为你原有的 writeLogTxtStr 或保持现有逻辑)
    private void writeLogToFile(String content, String logPath) {
        try {
            File logFile = new File(logPath);
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            // 追加模式写入
            try (FileWriter fw = new FileWriter(logFile, true)) {
                fw.write("=== [" + new Date() + "] ===\n");
                fw.write(content);
                fw.write("\n\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GateHisDataTask() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ST_GATE_RNEWPojo> rnewPojo = gateRnewData.selectList(null, null, null);
        List<ST_STBPRP_BDto> bDtoList = stbprpBData.GetSyncSTCDByType("3", null);
        for (ST_STBPRP_BDto dto : bDtoList) {
            String stcd = dto.getSTCD();
            String gtop = dto.getGTOPNUM() != null ? dto.getGTOPNUM() : "0";
            String omc = dto.getOMCNUM() != null ? dto.getOMCNUM() : "0";
            if ((int) Double.parseDouble(gtop) > 0) {// 闸坝开度
                for (int i = 1; i <= (int) Double.parseDouble(gtop); i++) {
                    String staionid = stcd + "2" + i;
                    if (rnewPojo.size() > 0) {
                        int finalI = i;
                        List<ST_GATE_RNEWPojo> gateNemF = rnewPojo.stream()
                                .filter(m -> m.getSTCD().equals(stcd) && "1".equals(m.getEQPNO())
                                        && m.getEXKEY().equals(String.valueOf(finalI)))
                                .collect(Collectors.toList());
                        if (gateNemF.size() > 0) {
                            for (ST_GATE_RNEWPojo gateNew : gateNemF) {
                                if ("闸坝开度".equals(gateNew.getEQPTP())) {
                                    SyncGqDataGATE(staionid, gateNew.getTM(), "1", String.valueOf(i), "闸坝开度", dto);
                                }
                            }
                        } else {
                            SyncGqDataGATE(staionid, null, "1", String.valueOf(i), "闸坝开度", dto);
                        }
                    } else {
                        SyncGqDataGATE(staionid, null, "1", String.valueOf(i), "闸坝开度", dto);
                    }
                }
            }
            if ((int) Double.parseDouble(omc) > 0) {// 泵站状态
                for (int i = 1; i <= (int) Double.parseDouble(omc); i++) {
                    String staionid = stcd + "3" + i;
                    if (rnewPojo.size() > 0) {
                        int finalI = i;
                        List<ST_GATE_RNEWPojo> gateNemF = rnewPojo.stream()
                                .filter(m -> m.getSTCD().equals(stcd) && "2".equals(m.getEQPNO())
                                        && m.getEXKEY().equals(String.valueOf(finalI)))
                                .collect(Collectors.toList());
                        if (gateNemF.size() > 0) {
                            for (ST_GATE_RNEWPojo gateNew : gateNemF) {
                                if ("泵站状态".equals(gateNew.getEQPTP())) {
                                    SyncGqDataGATE(staionid, gateNew.getTM(), "2", String.valueOf(i), "泵站状态", dto);
                                }
                            }
                        } else {
                            SyncGqDataGATE(staionid, null, "2", String.valueOf(i), "泵站状态", dto);
                        }
                    } else {
                        SyncGqDataGATE(staionid, null, "2", String.valueOf(i), "泵站状态", dto);
                    }
                }
            }
        }
        Integer num = gateRnewData.deleteAll();
        List<ST_GATE_RNEWPojo> gateNew = gateRData.selectNewList().stream().map(m -> {
            ST_GATE_RNEWPojo newObj = new ST_GATE_RNEWPojo();
            BeanUtils.copyProperties(m, newObj);
            return newObj;
        }).collect(Collectors.toList());
        if (num > 0) {
            Integer integer = gateRnewData.insertALL(gateNew);
            System.out.println("===========最新数据同步：" + integer + "条===========");
        }
    }

    public void SyncGqDataGATE(String staionid, String tm, String type, String exkey, String eqptp,
            ST_STBPRP_BDto dto) {
        if (!(null != tm && !"".equals(tm))) {
            tm = (new Date().getYear() + 1900) + "-01-01 00:00:00";
        }
        List<RTSQBZKDPojo> rtsqbzkdList = rtsqbzkdData.selectList(Collections.singletonList(staionid), tm, "");
        InsertGQData(rtsqbzkdList, dto.getSTCD(), dto.getSTNM(), exkey, type, eqptp, tm, false);
    }

    private void InsertGQData(List<RTSQBZKDPojo> objList, String stcd, String stnm, String exkey, String type,
            String eqptp, String initTM, boolean flag) {
        List<ST_GATE_RPojo> dtoList = new ArrayList<>();
        final String[] DateTime = { initTM };
        if (objList.size() > 0) {
            objList.forEach(m -> {
                String tm = m.getTIME();
                if (!DateTime[0].equals(tm)) {
                    ST_GATE_RPojo dto = new ST_GATE_RPojo();
                    dto.setSTCD(stcd);
                    dto.setEXKEY(exkey);
                    dto.setEQPNO(type);
                    dto.setEQPTP(eqptp);
                    dto.setTM(tm);
                    Double gtq = m.getFACTV();
                    if (null != gtq) {
                        if ("闸坝开度".equals(eqptp)) {
                            if (gtq < 0.05) {
                                gtq = 0.0;
                            }
                        }
                    }
                    dto.setGTQ(gtq);
                    dtoList.add(dto);
                }
                DateTime[0] = tm;
            });
        }
        Integer num = 0;
        List<ST_GATE_RPojo> subList = null;
        int count = 2000;
        int numsize = dtoList.size() / count;
        if (dtoList.size() % count != 0) {
            numsize += 1;
        }
        for (int i = 0; i < numsize; i++) {
            if (i == numsize - 1) {
                subList = dtoList.subList(count * i, dtoList.size());
            } else {
                subList = dtoList.subList(count * i, count * (i + 1));
            }
            num += gateRData.insertALL(subList);
        }
    }

    public void TaiFengJsonTask(String url, String stcd, String type, List<String> param, List<String> paramValue) {
        String json = null;
        FileOutputStream outputStream = null;
        try {
            json = wbcHttp(url, param, paramValue);
            System.out.println(json);
            String tm = "";
            Map map = JSON.parseObject(json, Map.class);
            if (map.containsKey("obj")) {
                Map obj = JSON.parseObject(map.get("obj").toString(), Map.class);
                if (obj.containsKey("runTime")) {
                    tm = obj.get("runTime").toString();
                }
                String fileName = paramValue.get(1) + "_" + stcd + "_" + tm + ".json";
                File file = new File(filePathName + "TaiFengJson");
                if (!file.exists()) {
                    file.mkdirs();
                }
                outputStream = new FileOutputStream(new File(filePathName + "TaiFengJson/" + fileName));
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void TaiFengLJTask(String year) {
        String url = typhoonUrl + "Api/TyphoonList/" + year;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String logFileName = "台风同步" + LocalDateTime.now().format(dateFormatter) + ".txt";
        writeLogTxtStr("========== 台风路径同步开始, 年份: " + year + " ==========", logFileName);
        List<String> param = null;
        List<String> paramValue = null;
        downAndParseTFList(url, param, paramValue, logFileName);
        List<ZJ_TFPojo> zjTfPojoList = parseTFList(url, param, paramValue);
        writeLogTxtStr("台风列表获取完成, 共 " + zjTfPojoList.size() + " 个台风待同步路径点", logFileName);
        int index = 0;
        for (ZJ_TFPojo zjTfPojo : zjTfPojoList) {
            index++;
            String tfId = zjTfPojo.getZJ_TFBH();
            writeLogTxtStr("[" + index + "/" + zjTfPojoList.size() + "] 开始同步台风路径点, 编号: "
                    + tfId + ", 名称: " + zjTfPojo.getZJ_TFM(), logFileName);
            String urlT = typhoonUrl + "Api/TyphoonInfo/" + tfId;
            List<String> paramT = null;
            List<String> paramValueT = null;
            downAndParseTFPoint(urlT, paramT, paramValueT, tfId, logFileName);
        }
        writeLogTxtStr("========== 台风路径同步结束, 年份: " + year + " ==========", logFileName);
    }

    public void downAndParseTFList(String url, List<String> param, List<String> paramValue, String logFileName) {
        List<ZJ_TFPojo> zjTfPojoList = new ArrayList<>();
        int newCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        try {
            String json = wbcHttp(url, param, paramValue);
            List<Map> mapList = JSON.parseArray(json, Map.class);
            writeLogTxtStr("从API获取到 " + mapList.size() + " 个台风记录", logFileName);
            int tfNum = 0;
            Integer maxId = 0;
            for (Map map : mapList) {
                if (map.containsKey("tfid") && !"".equals(map.get("tfid"))) {
                    List<ZJ_TFPojo> tfList = zj_tfData.selectList(null, null, null, null, null,
                            Collections.singletonList(map.get("tfid").toString()), null, null);
                    if (tfList.size() > 0) {
                        // 更新已存在台风的名称、活跃状态等可能变化的字段
                        ZJ_TFPojo existingPojo = tfList.get(0);
                        if (existingPojo.getZJ_BEDIT() == null || existingPojo.getZJ_BEDIT() == 0) {
                            boolean needUpdate = false;
                            String newName = map.get("name") != null ? map.get("name").toString() : "";
                            String newEnName = map.get("enname") != null ? map.get("enname").toString() : "";
                            Integer newIsCompleted = map.get("isactive") != null
                                    ? Integer.valueOf(map.get("isactive").toString()) : null;
                            String newStartTime = map.get("starttime") != null ? map.get("starttime").toString() : "";
                            String newEndTime = map.get("endtime") != null ? map.get("endtime").toString() : "";

                            if (!newName.equals(existingPojo.getZJ_TFM())) {
                                writeLogTxtStr("台风 " + existingPojo.getZJ_TFBH() + " 名称变更: "
                                        + existingPojo.getZJ_TFM() + " -> " + newName, logFileName);
                                existingPojo.setZJ_TFM(newName);
                                needUpdate = true;
                            }
                            if (!newEnName.equals(existingPojo.getZJ_TFME())) {
                                writeLogTxtStr("台风 " + existingPojo.getZJ_TFBH() + " 英文名变更: "
                                        + existingPojo.getZJ_TFME() + " -> " + newEnName, logFileName);
                                existingPojo.setZJ_TFME(newEnName);
                                needUpdate = true;
                            }
                            if (newIsCompleted != null && !newIsCompleted.equals(existingPojo.getZJ_ISCOMPLETED())) {
                                writeLogTxtStr("台风 " + existingPojo.getZJ_TFBH() + " 活跃状态变更: "
                                        + existingPojo.getZJ_ISCOMPLETED() + " -> " + newIsCompleted, logFileName);
                                existingPojo.setZJ_ISCOMPLETED(newIsCompleted);
                                needUpdate = true;
                            }
                            if (!newStartTime.equals(existingPojo.getZJ_TFDATE())) {
                                existingPojo.setZJ_TFDATE(newStartTime);
                                needUpdate = true;
                            }
                            if (!newEndTime.equals(existingPojo.getZJ_REMARK())) {
                                existingPojo.setZJ_REMARK(newEndTime);
                                needUpdate = true;
                            }
                            if (needUpdate) {
                                zj_tfData.updateOne(existingPojo);
                                updateCount++;
                            }
                        } else {
                            skipCount++;
                        }
                        continue;
                    }
                    ZJ_TFPojo pojo = new ZJ_TFPojo();
                    if (tfNum == 0) {
                        maxId = zj_tfData.SelectMaxId();
                    }

                    if (maxId == null)
                        maxId = 0;
                    tfNum += 1;
                    pojo.setZJ_ID(maxId + tfNum);
                    pojo.setZJ_TFBH(map.get("tfid").toString());
                    pojo.setZJ_TFM(map.get("name").toString());
                    pojo.setZJ_TFME(map.get("enname").toString());
                    pojo.setZJ_BEDIT(0);
                    pojo.setZJ_ISCOMPLETED(Integer.valueOf(map.get("isactive").toString()));
                    pojo.setZJ_TFDATE(map.get("starttime").toString());
                    pojo.setZJ_REMARK(map.get("endtime").toString());
                    zjTfPojoList.add(pojo);
                }
            }
            if (zjTfPojoList.size() > 0) {
                zj_tfData.insertALL(zjTfPojoList);
                newCount = zjTfPojoList.size();
            }
            writeLogTxtStr("台风列表同步完成, 新增: " + newCount + ", 更新: " + updateCount
                    + ", 跳过(人工编辑): " + skipCount + ", 总计: " + mapList.size(), logFileName);
        } catch (IOException e) {
            writeLogTxtStr("台风列表同步异常: " + e.getMessage(), logFileName);
            e.printStackTrace();
        }
    }

    public List<ZJ_TFPojo> parseTFList(String url, List<String> param, List<String> paramValue) {
        List<ZJ_TFPojo> zjTfPojoList = new ArrayList<>();
        try {
            String json = wbcHttp(url, param, paramValue);
            List<Map> mapList = JSON.parseArray(json, Map.class);
            int tfNum = 0;
            for (Map map : mapList) {
                if (map.containsKey("tfid") && !"".equals(map.get("tfid"))) {
                    ZJ_TFPojo pojo = new ZJ_TFPojo();
                    // Integer maxId = zj_tfData.SelectMaxId();
                    // pojo.setZJ_ID(maxId + (tfNum + 1));
                    pojo.setZJ_TFBH(map.get("tfid").toString());
                    pojo.setZJ_TFM(map.get("name").toString());
                    pojo.setZJ_TFME(map.get("enname").toString());
                    pojo.setZJ_BEDIT(0);
                    pojo.setZJ_ISCOMPLETED(Integer.valueOf(map.get("isactive").toString()));
                    pojo.setZJ_TFDATE(map.get("starttime").toString());
                    pojo.setZJ_REMARK(map.get("endtime").toString());
                    zjTfPojoList.add(pojo);
                    tfNum++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zjTfPojoList;
    }

    public void GetTFXSTask() {
        int distance = 100;
        int count = 10;
        List<ZJ_TFPojo> tfbhList = zj_tfData.selectTFBHList();
        for (ZJ_TFPojo tfObj : tfbhList) {
            List<ZJ_TFPojo> zjTfPojoList = zj_tfData.selectListByTfbh(tfObj.getZJ_TFBH());
            GetXiangsi(zjTfPojoList, tfbhList, distance, count);
        }
    }

    public void GetXiangsiToWX(List<ZJ_TFPojo> tfObjList, List<ZJ_TFPojo> tfObjAll, Integer distance, Integer count) {
        for (ZJ_TFPojo obj : tfObjList) {
            List<ZJ_TFLSLJPojo> dtZJLj = zj_tflsljData.selectList(obj.getZJ_TFBH());
            for (ZJ_TFPojo tfObj : tfObjAll) {
                if (!obj.getZJ_TFBH().equals(tfObj.getZJ_TFBH())) {
                    int sum_Count = 0;
                    List<ZJ_TFLSLJPojo> dtLj = zj_tflsljData.selectList(tfObj.getZJ_TFBH());
                    for (ZJ_TFLSLJPojo lsLjObj : dtZJLj) {
                        for (ZJ_TFLSLJPojo disLsLjObj : dtLj) {
                            double getDistance = PointsDistance.GetDistance(disLsLjObj.getZJ_WD(),
                                    disLsLjObj.getZJ_JD(), lsLjObj.getZJ_WD(), lsLjObj.getZJ_JD());
                            if (distance >= getDistance) {
                                sum_Count++;
                            }
                        }
                    }
                    if (sum_Count > count) {
                        List<ZJ_XSPojo> num1 = zj_xsData.selectList(obj.getZJ_TFBH(), tfObj.getZJ_TFBH(), null);
                        if (num1.size() == 0) {
                            ZJ_XSPojo xsobj = new ZJ_XSPojo();
                            xsobj.setPTFBH(Double.valueOf(obj.getZJ_TFBH()));
                            xsobj.setTFBH(Double.valueOf(tfObj.getZJ_TFBH()));
                            xsobj.setTYPE("相似台风");
                            zj_xsData.insertToOne(xsobj);
                        }
                        List<ZJ_XSPojo> num2 = zj_xsData.selectList(tfObj.getZJ_TFBH(), obj.getZJ_TFBH(), null);
                        if (num2.size() == 0) {
                            ZJ_XSPojo xsobj = new ZJ_XSPojo();
                            xsobj.setPTFBH(Double.valueOf(tfObj.getZJ_TFBH()));
                            xsobj.setTFBH(Double.valueOf(obj.getZJ_TFBH()));
                            xsobj.setTYPE("相似台风");
                            zj_xsData.insertToOne(xsobj);
                        }
                    }
                }
            }
        }
    }

    public void GetXiangsi(List<ZJ_TFPojo> tfObjList, List<ZJ_TFPojo> tfObjAll, Integer distance, Integer count) {
        for (ZJ_TFPojo obj : tfObjList) {
            List<ZJ_TFLSLJPojo> dtZJLj = zj_tflsljData.selectList(obj.getZJ_TFBH());
            for (ZJ_TFPojo tfObj : tfObjAll) {
                if (!obj.getZJ_TFBH().equals(tfObj.getZJ_TFBH())) {
                    int sum_Count = 0;
                    double distanceCount = 0.0, distanceMin = distance + 1, distanceRate = 50.0;
                    List<ZJ_TFLSLJPojo> dtLj = zj_tflsljData.selectList(tfObj.getZJ_TFBH());
                    for (ZJ_TFLSLJPojo lsLjObj : dtZJLj) {
                        distanceMin = 101;
                        for (ZJ_TFLSLJPojo disLsLjObj : dtLj) {
                            double getDistance = PointsDistance.GetDistance(disLsLjObj.getZJ_WD(),
                                    disLsLjObj.getZJ_JD(), lsLjObj.getZJ_WD(), lsLjObj.getZJ_JD());
                            if (distance >= getDistance) {
                                if (getDistance < distanceMin) {
                                    distanceMin = getDistance;
                                }
                                // sum_Count++;
                            }
                        }
                        if (distanceMin < 101) {
                            sum_Count++;
                            distanceCount += distanceMin;
                        }
                    }
                    if (sum_Count > count) {
                        distanceRate = GetSimilarLJ(distanceCount / sum_Count);
                        if (distanceRate >= 40) {
                            List<ZJ_XSPojo> num1 = zj_xsData.selectList(obj.getZJ_TFBH(), tfObj.getZJ_TFBH(), "true");
                            ZJ_XSPojo xsobj = new ZJ_XSPojo();
                            xsobj.setPTFBH(Double.valueOf(obj.getZJ_TFBH()));
                            xsobj.setTFBH(Double.valueOf(tfObj.getZJ_TFBH()));
                            xsobj.setTYPE("相似台风");
                            xsobj.setTFXIANGSIDU(distanceRate);
                            xsobj.setTFNOTE(String.valueOf(sum_Count));
                            if (num1.size() == 0) {
                                zj_xsData.insertToOne(xsobj);
                            } else {
                                zj_xsData.upDateToOne(xsobj);
                            }
                            List<ZJ_XSPojo> num2 = zj_xsData.selectList(tfObj.getZJ_TFBH(), obj.getZJ_TFBH(), "true");
                            ZJ_XSPojo xsobj2 = new ZJ_XSPojo();
                            xsobj2.setPTFBH(Double.valueOf(tfObj.getZJ_TFBH()));
                            xsobj2.setTFBH(Double.valueOf(obj.getZJ_TFBH()));
                            xsobj2.setTYPE("相似台风");
                            xsobj2.setTFXIANGSIDU(distanceRate);
                            xsobj2.setTFNOTE(String.valueOf(sum_Count));
                            if (num2.size() == 0) {
                                zj_xsData.insertToOne(xsobj2);
                            } else {
                                zj_xsData.upDateToOne(xsobj2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Double GetSimilarLJ(Double distance) {
        Double SimilarRate = Double.parseDouble("60");
        if (distance <= 30) {
            SimilarRate = Double.parseDouble("90");
        } else if (distance > 30 && distance <= 50) {
            SimilarRate = Double.parseDouble("85");
        } else if (distance > 50 && distance <= 70) {
            SimilarRate = Double.parseDouble("80");
        } else if (distance > 70 && distance <= 90) {
            SimilarRate = Double.parseDouble("70");
        }

        return SimilarRate;
    }

    private static class PointsDistance {
        private static final double EARTH_RADIUS = 6378.137;

        private static double rad(double d) {
            return d * Math.PI / 180.0;
        }

        // 纬度、经度
        public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                    Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000) / 10000;
            return s;
        }
    }

    public void downAndParseTFPoint(String url, List<String> param, List<String> paramValue,
            String tfId, String logFileName) {
        try {
            List<ZJ_TFLSLJPojo> lsList = new ArrayList<>();
            List<ZJ_TFYBLJPojo> ybList = new ArrayList<>();
            Integer maxId = 0;
            Integer maxIdT = 0;
            int lsNum = 0;
            int ybNum = 0;
            String json = wbcHttp(url, param, paramValue);
            Map map = JSON.parseObject(json, Map.class);
            if (map.containsKey("points") && null != map.get("points")) {
                String points = map.get("points").toString();
                if ("".equals(points) || null == points)
                    return;
                List<Map> mapList = JSON.parseArray(points, Map.class);
                for (Map map1 : mapList) {
                    String rqsj = map1.get("time") != null ? map1.get("time").toString() : "";
                    Integer count = zj_tflsljData.selectCount(tfId, rqsj);
                    // if (count > 0) continue;
                    String jd = map1.get("lng") != null ? map1.get("lng").toString() : "0";
                    String wd = map1.get("lat") != null ? map1.get("lat").toString() : "0";
                    String strong = map1.get("strong") != null ? map1.get("strong").toString() : "";
                    String power = map1.get("power") != null ? map1.get("power").toString() : "";
                    String zxfs = map1.get("speed") != null ? map1.get("speed").toString() : "0";
                    String zxqy = map1.get("pressure") != null && map1.get("pressure").toString().trim().length() > 0
                            ? map1.get("pressure").toString().trim()
                            : "0";
                    String ydsd = map1.get("movespeed") != null ? map1.get("movespeed").toString() : "0";
                    String ydfx = map1.get("movedirection") != null ? map1.get("movedirection").toString() : "";
                    String radius7 = map1.get("radius7") != null ? map1.get("radius7").toString() : "";
                    String radius10 = map1.get("radius10") != null ? map1.get("radius10").toString() : "";
                    String radius12 = map1.get("radius12") != null ? map1.get("radius12").toString() : "";
                    ZJ_TFLSLJPojo lsObj = new ZJ_TFLSLJPojo();
                    if (lsNum == 0) {
                        maxId = zj_tflsljData.selectMaxId();
                    }
                    if (maxId == null)
                        maxId = 0;
                    lsObj.setZJ_TFBH(tfId);
                    lsObj.setZJ_RQSJ(rqsj);
                    lsObj.setZJ_JD(Float.valueOf(jd));
                    lsObj.setZJ_WD(Float.valueOf(wd));
                    lsObj.setZJ_ZXFS(Integer.valueOf(zxfs));
                    lsObj.setZJ_ZXQY(Integer.valueOf(zxqy));
                    lsObj.setZJ_YDSD(Integer.valueOf(ydsd));
                    lsObj.setZJ_YDFX(ydfx);
                    lsObj.setZJ_Radius7(radius7);
                    lsObj.setZJ_Radius10(radius10);
                    lsObj.setZJ_Radius12(radius12);
                    if (!(count > 0)) {
                        lsNum += 1;
                        lsObj.setZJ_ID(maxId + lsNum);
                        lsList.add(lsObj);
                    }
                    if (map1.containsKey("forecast")) {
                        String forecast = map1.get("forecast").toString();
                        if ("".equals(forecast) || null == forecast)
                            continue;
                        List<Map> maps = JSON.parseArray(forecast, Map.class);
                        for (Map m : maps) {// List list ThreadPoolTaskScheduled
                            String tm = "";//
                            if (m.containsKey("tm")) {
                                tm = m.get("tm").toString();
                            }
                            if (m.containsKey("forecastpoints")) {
                                String forecastpoints = m.get("forecastpoints").toString();
                                if ("".equals(forecastpoints) || null == forecastpoints)
                                    continue;
                                List<Map> list = JSON.parseArray(forecastpoints, Map.class);

                                for (Map mObj : list) {
                                    String time = mObj.get("time") != null ? mObj.get("time").toString() : "";
                                    String lng = mObj.get("lng") != null ? mObj.get("lng").toString() : "0";
                                    String lat = mObj.get("lat") != null ? mObj.get("lat").toString() : "0";
                                    String strongTwo = mObj.get("strong") != null ? mObj.get("strong").toString() : "";
                                    String powerTwo = mObj.get("power") != null ? mObj.get("power").toString() : "";
                                    String speed = mObj.get("speed") != null ? mObj.get("speed").toString() : "0";
                                    String pressure = mObj.get("pressure") != null
                                            && mObj.get("pressure").toString().trim().length() > 0
                                                    ? mObj.get("pressure").toString()
                                                    : "0";
                                    String ybsj = mObj.get("ybsj") != null ? mObj.get("ybsj").toString() : "";
                                    if (!"".equals(ybsj)) {
                                        LocalDateTime parse = LocalDateTime.parse(ybsj,
                                                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                                                .ofPattern("yyyy-MM-dd HH:mm:ss");
                                        ybsj = dateTimeFormatter.format(parse);
                                        Integer count1 = zj_tfybljData.selectCount(tfId, time, ybsj, tm);
                                        if (count1 > 0)
                                            continue;
                                        ZJ_TFYBLJPojo ybObj = new ZJ_TFYBLJPojo();
                                        if (ybNum == 0) {
                                            maxIdT = zj_tfybljData.selectMaxId();
                                        }
                                        if (maxIdT == null)
                                            maxIdT = 0;
                                        ybNum += 1;
                                        ybObj.setZJ_ID(maxIdT + ybNum);
                                        ybObj.setZJ_TFBH(tfId);
                                        ybObj.setZJ_TM(tm);
                                        ybObj.setZJ_RQSJ(time);
                                        ybObj.setZJ_JD(Float.valueOf(lng));
                                        ybObj.setZJ_WD(Float.valueOf(lat));
                                        ybObj.setZJ_ZXFS(Integer.valueOf(speed));
                                        ybObj.setZJ_ZXQY(Integer.valueOf(pressure));
                                        ybObj.setZJ_YBSJ(ybsj);
                                        ybList.add(ybObj);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int count = 3000;
            int lsSize = lsList.size() / count;
            if (lsList.size() % count != 0)
                lsSize += 1;
            int ybSize = ybList.size() / count;
            if (ybList.size() % count != 0)
                ybSize += 1;
            List<ZJ_TFLSLJPojo> lsListT = new ArrayList<>();
            List<ZJ_TFYBLJPojo> ybListT = new ArrayList<>();
            for (int i = 0; i < lsSize; i++) {
                if (i == lsSize - 1) {
                    lsListT = lsList.subList(i * count, lsList.size());
                } else {
                    lsListT = lsList.subList(i * count, (i + 1) * count);
                }
                zj_tflsljData.insertALL(lsListT);
            }
            for (int i = 0; i < ybSize; i++) {
                if (i == ybSize - 1) {
                    ybListT = ybList.subList(i * count, ybList.size());
                } else {
                    ybListT = ybList.subList(i * count, (i + 1) * count);
                }
                zj_tfybljData.insertALL(ybListT);
            }
            writeLogTxtStr("台风 " + tfId + " 路径点同步完成, 新增历史路径: " + lsList.size()
                    + " 条, 新增预报路径: " + ybList.size() + " 条", logFileName);

        } catch (Exception e) {
            writeLogTxtStr("台风 " + tfId + " 路径点同步异常: " + e.getMessage(), logFileName);
            e.printStackTrace();
        }
    }

    // 模型自动计算
    public void AutomaticCalculation() throws IOException {
        // *********** 去数据库查看今天有没有自动计算过
        try {
            List<DD_AUTOMATICPojo> listTic = dd_automaticData.selectList(null, "1", null, null);
            int hour = LocalDateTime.now().getHour();
            listTic = listTic.stream()
                    .filter(p -> Integer.parseInt(p.getVALUE()) >= hour)
                    .sorted((u1, u2) -> Integer.compare(Integer.parseInt(u1.getVALUE()),
                            Integer.parseInt(u2.getVALUE())))
                    .collect(Collectors.toList());

            writeLogTxtStr("AutomaticCalculation：listTic的长度为" + listTic.size() + "，hour为" + hour,
                    "AutomaticCalculation.txt");
            if (listTic.size() > 0) {
                String jsvalue = listTic.get(0).getVALUE();
                // 使用String.format确保小时部分两位数
                String dateTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + " " + String.format("%02d", Integer.parseInt(jsvalue)) + ":00:00";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime DD_TM = LocalDateTime.parse(dateTimeStr, formatter);
                System.out.println("AutomaticCalculation：时间DD_TM(" + DD_TM + ")");
                writeLogTxtStr("AutomaticCalculation：时间DD_TM(" + DD_TM + ")", "AutomaticCalculation.txt");
                long mites = java.time.Duration.between(DD_TM, LocalDateTime.now()).toMinutes();

                if (mites >= 20) { // 不能整点去计算，以防数据没来
                    String stime = DD_TM.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    List<DD_SOLUTIONPojo> listAUTO = dd_solutionData.selectList(null, null, null, stime, stime, "1",
                            null, null);
                    boolean isSucess = listAUTO.isEmpty();
                    if (!isSucess) {
                        System.out.println("AutomaticCalculation："
                                + DD_TM.format(DateTimeFormatter.ofPattern("yyyy年M月d日H时")) + "方案已经自动计算过了！("
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时")) + ")");
                        writeLogTxtStr("AutomaticCalculation："
                                + DD_TM.format(DateTimeFormatter.ofPattern("yyyy年M月d日H时")) + "方案已经自动计算过了！("
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时")) + ")",
                                "AutomaticCalculation.txt");
                    }
                    if (isSucess) {
                        // LocalDateTime time = LocalDateTime.now().plusHours(hour);
                        // System.out.println("模型开始计算依据时间：" +
                        // LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时")) +
                        // "的方案(" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        // ")");
                        // String url = ClientIP + "DD_MX/AutomaticCalculation.html?tongbuZDMD=0&time="
                        // + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        // openBrowser(url);
                        String scwdatatype = "";
                        if (listTic.get(0).getNOTE().indexOf("@") > -1) {
                            String[] noteArr = listTic.get(0).getNOTE().split("@");
                            scwdatatype = noteArr[0];
                        }
                        String token = getToken("", "");
                        // 调用模型计算
                        String parmasMap = "{\"tm\": \"" + stime + "\",\"fpdr\":\"" + listTic.get(0).getNUM()
                                + "\",\"gcdatatype\": \"DDFN\",\"jydatatype\": \"" + listTic.get(0).getNOTE()
                                + "\",\"scwdatatype\":\"" + scwdatatype + "\"}";
                        HashMap<String, Object> header = new HashMap<>();
                        header.put("Content-Type", "application/json;charset=UTF-8");
                        header.put("Authorization", token);
                        String result = apihelper.apipost(ServerIP + "SWZZ_MODE_ES_ZHANDIANDATA/startHuishuiJisuan",
                                parmasMap, header);
                        System.out.println("AutomaticCalculation：接口返回结果是：" + result);
                        writeLogTxtStr("AutomaticCalculation：接口返回结果是：" + result, "AutomaticCalculation.txt");
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> mapList = new HashMap<>();
                        try {
                            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
                            });
                            int total = (int) mapList.get("total");
                            System.out.println("AutomaticCalculation：IsSuccess（" + total + ")");
                            if (total > 0) {
                                System.out
                                        .println("AutomaticCalculation：模型计算成功（参数：" + parmasMap + "），接口返回结果是：" + result);
                                writeLogTxtStr("AutomaticCalculation：模型计算成功（参数：\"+parmasMap+\"），接口返回结果是：" + result,
                                        "AutomaticCalculation.txt");
                            } else {
                                System.out.println("AutomaticCalculation：模型报错，接口返回结果是：" + result);
                                writeLogTxtStr("AutomaticCalculation：模型报错，接口返回结果是：" + result,
                                        "AutomaticCalculation.txt");
                            }
                        } catch (IOException e) {
                            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLogTxtStr("AutomaticCalculation：报错" + e.getMessage(), "AutomaticCalculation.txt");
        }
        // *********** 去数据库查看今天有没有自动计算过
    }

    public void AutomaticCalculationFORECAST() throws IOException {
        // *********** 去数据库查看今天有没有自动计算过
        try {
            String token = getToken("", "");
            // 调用模型计算
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);

            // 先去插入数据*****************************
            String resulthighlo = apihelper.apipost(ServerIP + "SWZZ_MODE_ES_TIDALFORECAST/restockhighlow", parmasMap,
                    header);
            // 先去插入数据*****************************

            String result = apihelper.apipost(ServerIP + "SWZZ_MODE_ES_TIDALFORECAST/restock", parmasMap, header);
            System.out.println("restock：接口返回结果是：" + result);
            writeLogTxtStr("restock：接口返回结果是：" + result, "AutomaticCalculationFORECAST.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AutomaticCalculationSWIC() throws IOException {
        // *********** 去数据库查看今天有没有自动计算过
        try {
            String token = getToken("", "");
            // 调用模型计算
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "SWZZ_MODE_ST_FORECAST_F/SwicSQYBCG", parmasMap, header);
            System.out.println("SwicSQYBCG：接口返回结果是：" + result);
            writeLogTxtStr("SwicSQYBCG：接口返回结果是：" + result, "AutomaticCalculationSWIC.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定期删除文件夹
    public void removeAllQXFile() throws IOException {
        try {
            // 定义时间格式(yyyyMMddHHmmss)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            // 获取当前日期
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();

            // 日期减去3天
            cal.add(Calendar.DAY_OF_MONTH, -3);
            Date newDate = cal.getTime();

            String folderPath = filePathName + "fq/FQ";
            List<File> fileList = FilePathUtils.getAllFiles(folderPath);
            fileList.forEach(file -> {
                String fileName = file.getName();
                String timeStamp = fileName.substring(14, 27);
                // 转换为Date对象
                try {
                    Date date = sdf.parse(timeStamp);
                    if (date.getTime() < newDate.getTime()) {// 删除文件
                        file.delete();
                    }
                    System.out.println("fileName：" + fileName);
                } catch (ParseException e) {
                    // throw new RuntimeException(e);
                }
            });

            folderPath = filePathName + "fq/FQ6Hour";
            fileList = FilePathUtils.getAllFiles(folderPath);
            fileList.forEach(file -> {
                String fileName = file.getName();
                String timeStamp = fileName.substring(12, 24) + "00";
                // 转换为Date对象
                try {
                    Date date = sdf.parse(timeStamp);
                    if (date.getTime() < newDate.getTime()) {// 删除文件
                        file.delete();
                    }
                    System.out.println("fileName：" + fileName);
                } catch (ParseException e) {
                    System.out.println("FQ6Hour（" + timeStamp + "）时间转换报错" + e.getMessage());
                }
            });

            folderPath = filePathName + "FQNC/FQNC";
            fileList = FilePathUtils.getAllFiles(folderPath);
            fileList.forEach(file -> {
                String fileName = file.getName();
                String timeStamp = fileName.substring(14, 27);
                // 转换为Date对象
                try {
                    Date date = sdf.parse(timeStamp);
                    if (date.getTime() < newDate.getTime()) {// 删除文件
                        file.delete();
                    }
                    System.out.println("fileName：" + fileName);
                } catch (ParseException e) {
                    // throw new RuntimeException(e);
                }
            });
            System.out.println("所有文件已删除完成");
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            writeLogTxtStr(folderPath + "所有文件已删除完成", "removeAllQXFile" + formattedDateLog + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定期删除自动计算的方案：7天以前的
    public void removeAutoModeFang() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("removeAutoModeFang执行删除自动计算方案开始：", "removeAutoModeFang" + formattedDateLog + ".txt");
            String token = getToken("", "");
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "SWZZ_MODE_DD_SOLUTION/removeAutoModeFang", parmasMap, header);
            writeLogTxtStr("removeAutoModeFang执行删除自动计算方案结束", "removeAutoModeFang" + formattedDateLog + ".txt");
        } catch (Exception e) {
            // e.printStackTrace();
            writeLogTxtStr("删除方案报错", "removeAutoModeFang" + formattedDateLog + ".txt");
        }
    }

    private static void openBrowser(String url) {
        try {
            if (java.awt.GraphicsEnvironment.isHeadless()) {
                // 在无头环境中使用命令行打开浏览器
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.contains("win")) {
                    // Windows 系统
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (osName.contains("mac")) {
                    // macOS 系统
                    Runtime.getRuntime().exec("open " + url);
                } else {
                    // Linux 系统
                    Runtime.getRuntime().exec("xdg-open " + url);
                }
            } else {
                // 在支持图形界面的环境中使用 Desktop 类
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 写日志StringBuilder
    public void writeLogTxtBuilder(StringBuilder builder, String filename) {
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            String logStr = formattedDateLog + "：" + builder.toString() + "\n";
            byte[] bytes = logStr.getBytes(StandardCharsets.UTF_8);
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/" + filename);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 写日志String
    public void writeLogTxtStr(String logStr, String filename) {
        try {
            DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateLog = LocalDateTime.now();
            String formattedDateLog = currentDateLog.format(formatterYMDHM);
            logStr = formattedDateLog + "：" + logStr + "\n";
            byte[] bytes = logStr.getBytes(StandardCharsets.UTF_8);

            // 使用FileOutputStream构造方法的第二个参数true表示追加模式
            FileOutputStream out = new FileOutputStream(filePathName + "/logs/" + filename, true);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getToken(String qxLogin, String pwd) {
        String token = "";
        if (qxLogin.equals("")) {
            qxLogin = "swzz";
            pwd = "Gaoqi_0531";
        }
        String parmasMap = "{\"USERNAME\": \"" + qxLogin + "\",\"PWD\":\"" + pwd + "\"}";
        HashMap<String, Object> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=UTF-8");
        String result = apihelper.apipost(ServerIP + "SWZZ_DATA_employee/GetToken", parmasMap, header);
        System.out.println("getToken：接口返回结果是：" + result);
        writeLogTxtStr("getToken：接口返回结果是：" + result, "getToken.txt");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapList = new HashMap<>();
        try {
            mapList = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            token = mapList.get("data").toString();
            System.out.println("getToken：token（" + token + ")");
        } catch (IOException e) {
            System.out.println("调用" + parmasMap + "接口报错,接口返回结果是：" + result);
        }
        return token;
    }

    public void SynchronizeData() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeData开始同步：", "SynchronizeData" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"pathname\":\"1\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeData【潮（水）位】结束同步，result结果为*****" + result,
                    "SynchronizeData" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeData报错", "SynchronizeData" + formattedDateLog + ".txt");
        }
    }

    public void SynchronizeYLData() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeData开始同步：", "SynchronizeData" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"pathname\":\"2\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeData【雨量】结束同步，result结果为*****" + result,
                    "SynchronizeData" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeData报错", "SynchronizeData" + formattedDateLog + ".txt");
        }
    }

    public void SynchronizeQXYLData() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeQXYLData开始同步：", "SynchronizeData" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"datasource\":\"市气象局\",\"pathname\":\"2\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeQXYLData【市气象局雨量】结束同步，result结果为*****" + result,
                    "SynchronizeQXYLData" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeQXYLData报错", "SynchronizeData" + formattedDateLog + ".txt");
        }
    }

    public void SynchronizeLLData() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeData开始同步：", "SynchronizeData" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"pathname\":\"5,9\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeData【流量】结束同步，result结果为*****" + result,
                    "SynchronizeData" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeData报错", "SynchronizeData" + formattedDateLog + ".txt");
        }
    }

    public void SynchronizeFXData() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeData开始同步：", "SynchronizeData" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"pathname\":\"8\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeData【风速风向】结束同步，result结果为*****" + result,
                    "SynchronizeData" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeData报错", "SynchronizeData" + formattedDateLog + ".txt");
        }
    }

    // 水务平台--------------水位
    public void SynchronizeDataSWPT_SW() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeDataSWPT_SW水务平台实时水位开始同步：", "SynchronizeDataSWPT_SW" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"datasource\":\"水利部\",\"pathname\":\"1\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeDataSWPT_SW水务平台实时水位同步，result结果为*****" + result,
                    "SynchronizeDataSWPT_SW" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeDataSWPT_SW报错", "SynchronizeDataSWPT_SW" + formattedDateLog + ".txt");
        }
    }
    // 水务平台--------------水位

    // 水务平台--------------流量
    public void SynchronizeDataSWPT_LL() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("SynchronizeDataSWPT_LL水务平台实时流量开始同步：", "SynchronizeDataSWPT_LL" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"datasource\":\"水利部\",\"pathname\":\"5\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String result = apihelper.apipost(ServerIP + "GetWaterViewNew/SynchronizeData", parmasMap, header);
            writeLogTxtStr("SynchronizeDataSWPT_LL水务平台实时流量同步，result结果为*****" + result,
                    "SynchronizeDataSWPT_LL" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("SynchronizeDataSWPT_LL报错", "SynchronizeDataSWPT_LL" + formattedDateLog + ".txt");
        }
    }
    // 水务平台--------------流量

    public void SynchronizeDataGate() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("水闸数据开始同步：", "SynchronizeDataGate" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"datasource\":\"水利中心\",\"pathname\":\"3\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String apiurl = ServerIP + "GetWaterViewNew/SynchronizeData";
            writeLogTxtStr("请求地址：" + apiurl + "，参数：" + parmasMap, "SynchronizeDataGate" + formattedDateLog + ".txt");
            String result = apihelper.apipost(apiurl, parmasMap, header);
            writeLogTxtStr("水闸数据同步，result结果为*****" + result, "SynchronizeDataGate" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("水闸数据同步报错", "SynchronizeDataGate" + formattedDateLog + ".txt");
        }
    }

    public void SynchronizeDataBeng() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("防汛泵站数据开始同步：", "SynchronizeDataBeng" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{\"datasource\":\"市排水中心\",\"pathname\":\"3\"}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String apiurl = ServerIP + "GetWaterViewNew/SynchronizeData";
            writeLogTxtStr("请求地址：" + apiurl + "，参数：" + parmasMap, "SynchronizeDataBeng" + formattedDateLog + ".txt");
            String result = apihelper.apipost(apiurl, parmasMap, header);
            writeLogTxtStr("防汛泵站同步，result结果为*****" + result, "SynchronizeDataBeng" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("防汛泵站同步报错", "SynchronizeDataBeng" + formattedDateLog + ".txt");
        }
    }

    // 抓取放江模型的数据
    public void AutomaticFangjiangOverflow() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        writeLogTxtStr("放江量数据开始同步：", "SynchronizeDataFangjiang" + formattedDateLog + ".txt");
        try {
            String token = getToken("", "");
            // 调用模型计算
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);

            try {
                // 先去插入数据*****************************
                String resulthighlo = apihelper.apipost(ServerIP + "SWZZ_MODE_ES_TIDALFORECAST/getFangjiangOverflow",
                        parmasMap, header);
                // 先去插入数据*****************************
                Map<String, Object> mapList = new HashMap<>();
                ObjectMapper objectMapper = new ObjectMapper();
                mapList = objectMapper.readValue(resulthighlo, new TypeReference<Map<String, Object>>() {
                });
                int total = (int) mapList.get("total");
                if (total > 0) {
                    writeLogTxtStr("放江量数据同步成功", "SynchronizeDataFangjiang" + formattedDateLog + ".txt");
                } else {
                    writeLogTxtStr("没有放江量入库", "SynchronizeDataFangjiang" + formattedDateLog + ".txt");
                }
            } catch (Exception e) {
                writeLogTxtStr("放江量数据同步报错：" + e.getMessage(), "SynchronizeDataFangjiang" + formattedDateLog + ".txt");
            }

        } catch (Exception e) {
            writeLogTxtStr("放江量数据同步报错：" + e.getMessage(), "SynchronizeDataFangjiang" + formattedDateLog + ".txt");
        }
    }

    public void SyncDataYJXY() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("【应急响应】数据开始同步：", "SynchronizeDataYJXY" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String apiurl = ServerIP + "SWZZ_DATA_emergency_response/SyncDataYJXY";
            writeLogTxtStr("请求地址：" + apiurl + "，参数：" + parmasMap, "SynchronizeDataYJXY" + formattedDateLog + ".txt");
            String result = apihelper.apipost(apiurl, parmasMap, header);
            writeLogTxtStr("【应急响应】同步，result结果为*****" + result, "SynchronizeDataYJXY" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("【应急响应】同步报错", "SynchronizeDataYJXY" + formattedDateLog + ".txt");
        }
    }

    public void syncRecentPptnQixiang() throws IOException {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        try {
            writeLogTxtStr("【市气象局雨量补录】数据开始同步：", "syncRecentPptnQixiang" + formattedDateLog + ".txt");
            String token = "768ADC6A9E72BFEE4891F1F98650FEEE";
            String parmasMap = "{}";
            HashMap<String, Object> header = new HashMap<>();
            header.put("Content-Type", "application/json;charset=UTF-8");
            header.put("Authorization", token);
            String apiurl = ServerIP + "GetWaterViewNew/syncRecentPptnQixiang";
            writeLogTxtStr("请求地址：" + apiurl + "，参数：" + parmasMap, "syncRecentPptnQixiang" + formattedDateLog + ".txt");
            String result = apihelper.apipost(apiurl, parmasMap, header);
            writeLogTxtStr("【市气象局雨量补录】同步，result结果为*****" + result, "syncRecentPptnQixiang" + formattedDateLog + ".txt");
        } catch (Exception e) {
            writeLogTxtStr("【市气象局雨量补录】同步报错", "syncRecentPptnQixiang" + formattedDateLog + ".txt");
        }
    }

    // ==================== NWP面雨量预报同步 ====================

    /**
     * 同步NWP行政分区和水利片区面雨量预报数据
     * 6h(productId=12): 每5分钟可能有新起报时间，起报时间不同则插入新数据
     * 48h(productId=11): 起报时间段内数据会更新，采用查询-分拆-更新(无间隙)策略
     */
    public void syncNwpPolygonPrecip() {
        DateTimeFormatter formatterYMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime currentDateLog = LocalDateTime.now();
        String formattedDateLog = currentDateLog.format(formatterYMDHM);
        String logFile = "SyncNwpPolygonPrecip" + formattedDateLog + ".txt";

        try {
            // 先处理48h(productId=11)，再处理6h(productId=12)
            processNwpProduct(11, 48, logFile);
            processNwpProduct(12, 6, logFile);
        } catch (Exception e) {
            writeLogTxtStr("NWP面雨量同步异常: " + e.getMessage(), logFile);
            e.printStackTrace();
        }
    }

    /**
     * 处理单个预报产品
     * @param productId 11=48h, 12=6h
     * @param fpdr      预报时效
     * @param logFile   日志文件名
     */
    private void processNwpProduct(int productId, int fpdr, String logFile) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 1. 获取最新起报时间
        String initialTime = getNwpProductLatestTime(productId, logFile);
        if (initialTime == null || "".equals(initialTime)) {
            writeLogTxtStr("productId=" + productId + " 获取起报时间为空，跳过", logFile);
            return;
        }
        // 将ISO格式 "2026-07-31T08:00:00" 转换为 "2026-07-31 08:00:00"
        String ybtm = initialTime.replace("T", " ");
        writeLogTxtStr("productId=" + productId + " fpdr=" + fpdr + " 起报时间: " + ybtm, logFile);

        // 2. 检查是否已同步（查RLIST表）
        List<StRnflRlistPojo> existingRlist = stRnflRlistData.selectList(ybtm, (double) fpdr);
        boolean alreadyExists = existingRlist != null && existingRlist.size() > 0;

        // 6h: 已存在则跳过
        if (fpdr == 6 && alreadyExists) {
            writeLogTxtStr("productId=" + productId + " YBTM=" + ybtm + " 已同步，跳过", logFile);
            return;
        }

        // 3. 拉取行政分区(type=1)和水利分片(type=2)数据
        List<StRnflRmodePojo> allData = new ArrayList<>();
        allData.addAll(fetchNwpPrecipData(productId, initialTime, 1, "行政分区", fpdr, logFile));
        allData.addAll(fetchNwpPrecipData(productId, initialTime, 2, "水利分片", fpdr, logFile));

        if (allData.isEmpty()) {
            writeLogTxtStr("productId=" + productId + " 未获取到任何面雨量数据，跳过", logFile);
            return;
        }
        writeLogTxtStr("productId=" + productId + " 获取到 " + allData.size() + " 条面雨量数据", logFile);

        // 4. 入库RMODE数据
        if (fpdr == 48 && alreadyExists) {
            // 48h数据更新：查询已有数据 → 分拆insert/update → 先更新再插入（无间隙）
            for (String type : Arrays.asList("行政分区", "水利分片")) {
                List<StRnflRmodePojo> typeData = allData.stream()
                        .filter(d -> type.equals(d.getTYPE()))
                        .collect(Collectors.toList());
                if (typeData.isEmpty()) continue;
                upsertRmodeData(typeData, ybtm, type, fpdr, logFile);
            }
            // 更新RLIST的NCFILE
            StRnflRlistPojo rlistPojo = existingRlist.get(0);
            rlistPojo.setNCFILE("nwp/prePolygonForecast?productId=" + productId);
            stRnflRlistData.deleteByTmAndFpdr(ybtm, (double) fpdr);
        } else {
            // 6h新数据 或 48h首次：直接批量插入
            batchInsertRmode(allData, logFile);
        }

        // 5. 插入RLIST记录
        insertNwpRlist(ybtm, fpdr, productId, logFile);

        writeLogTxtStr("productId=" + productId + " fpdr=" + fpdr + " 同步完成，共 " + allData.size() + " 条", logFile);
    }

    /**
     * 获取最新起报时间
     */
    private String getNwpProductLatestTime(int productId, String logFile) {
        try {
            String url = waterlogging_api + "nwp/productLatestTime?productId=" + productId;
            writeLogTxtStr("请求起报时间: " + url, logFile);
            String json = PostHttp(url, null, null);
            writeLogTxtStr("productLatestTime返回: " + json, logFile);
            if (json != null) {
                Map map = JSON.parseObject(json, Map.class);
                if (map.containsKey("code") && (int) map.get("code") == 200) {
                    return map.get("data").toString();
                }
            }
        } catch (Exception e) {
            writeLogTxtStr("获取起报时间异常 productId=" + productId + ": " + e.getMessage(), logFile);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拉取面雨量预报数据并解析
     * @param productId    11=48h, 12=6h
     * @param initialTime  起报时间 (ISO格式: "2026-07-31T08:00:00")
     * @param type         1=行政分区, 2=水利分片
     * @param typeName     类型中文名
     * @param logFile      日志文件名
     * @return 解析后的RMODE数据列表
     */
    private List<StRnflRmodePojo> fetchNwpPrecipData(int productId, String initialTime, int type,
                                                      String typeName, int fpdr, String logFile) {
        List<StRnflRmodePojo> resultList = new ArrayList<>();
        try {
            String url = waterlogging_api + "nwp/prePolygonForecast?productId=" + productId
                    + "&productInitialTime=" + initialTime + "&type=" + type;
            writeLogTxtStr("请求面雨量: " + url, logFile);
            String json = PostHttp(url, null, null);
            if (json == null) {
                writeLogTxtStr("prePolygonForecast返回为空 type=" + typeName, logFile);
                return resultList;
            }
            Map map = JSON.parseObject(json, Map.class);
            if (!map.containsKey("code") || (int) map.get("code") != 200) {
                writeLogTxtStr("prePolygonForecast返回失败 type=" + typeName + " json=" + json, logFile);
                return resultList;
            }
            String dataStr = map.get("data").toString();
            List<Map> regionList = JSON.parseArray(dataStr, Map.class);
            if (regionList == null || regionList.isEmpty()) {
                writeLogTxtStr("prePolygonForecast data为空 type=" + typeName, logFile);
                return resultList;
            }

            // 解析每个区域的数据
            for (Map region : regionList) {
                String regionName = region.get("regionName") != null ? region.get("regionName").toString() : "";
                // 水利分片中存在regionName为空的"中心片"
                if ("".equals(regionName)) {
                    regionName = "中心片";
                }
                String ybtm = initialTime.replace("T", " ");  // "2026-07-31 08:00:00"

                Object rainValuesObj = region.get("rainValues");
                if (rainValuesObj == null) continue;
                List<Map> rainValues = JSON.parseArray(rainValuesObj.toString(), Map.class);
                if (rainValues == null) continue;

                // 按小时聚合：10分钟数据累加为小时雨量，以整点结束时间为TM
                // 48h: validTime已是整点(09:00)，天然1条/小时
                // 6h:  validTime是10分钟间隔(08:10~09:00)，6条累加→TM=09:00
                // key: "2026-07-31 09:00:00" (小时整点结束时间), value: 累加的gridValue
                Map<String, Double> hourMap = new LinkedHashMap<>();
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                for (Map rv : rainValues) {
                    String validTime = rv.get("validTime") != null ? rv.get("validTime").toString() : "";
                    Object gridValueObj = rv.get("gridValue");
                    Double gridValue = gridValueObj != null ? Double.parseDouble(gridValueObj.toString()) : 0.0;

                    // 计算该条数据归属于哪个整点结束时间
                    // 整点(e.g. 09:00:00) → 不动；非整点(e.g. 08:10:00) → 进到下一整点(09:00:00)
                    String tmKey;
                    try {
                        Date vtDate = isoFormat.parse(validTime);
                        if (vtDate.getMinutes() == 0 && vtDate.getSeconds() == 0) {
                            tmKey = validTime;  // 已是整点
                        } else {
                            // 进到下一整点
                            tmKey = isoFormat.format(new Date(vtDate.getTime() + 3600000L - vtDate.getMinutes() * 60000L - vtDate.getSeconds() * 1000L));
                        }
                    } catch (ParseException e) {
                        // 兜底：截取到小时
                        tmKey = validTime.length() >= 13 ? validTime.substring(0, 13) : validTime;
                    }
                    hourMap.merge(tmKey, gridValue, Double::sum);
                }

                // 为每个小时创建一条记录
                for (Map.Entry<String, Double> entry : hourMap.entrySet()) {
                    String tm = entry.getKey().replace("T", " ");  // "2026-07-31 09:00:00"
                    Double drp = entry.getValue();

                    StRnflRmodePojo pojo = new StRnflRmodePojo();
                    pojo.setSTCD(regionName);                       // STCD ← regionName
                    pojo.setYBTM(ybtm);                             // YBTM
                    pojo.setTM(tm);                                 // TM (预报时间，整点结束时间)
                    pojo.setDRP(drp);                               // DRP (小时累计降雨量)
                    pojo.setTYPE(typeName);                         // TYPE
                    pojo.setFPDR((double) fpdr);                    // FPDR (6或48)
                    // 非雨量字段留null
                    resultList.add(pojo);
                }
            }
            writeLogTxtStr("type=" + typeName + " 解析到 " + resultList.size() + " 条记录", logFile);
        } catch (Exception e) {
            writeLogTxtStr("获取面雨量异常 type=" + typeName + ": " + e.getMessage(), logFile);
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 无间隙更新RMODE数据
     * 先查询已有数据，分拆为insertList和updateList，先更新后插入
     */
    private void upsertRmodeData(List<StRnflRmodePojo> newData, String ybtm, String type, int fpdr, String logFile) {
        // 查询已有的RMODE数据（按YBTM + FPDR + TYPE过滤，区分6h/48h）
        List<StRnflRmodePojo> existingData = stRnflRmodeData.selectByYbtmAndFpdr(ybtm, (double) fpdr, type);

        // 构建已存在数据的key集合
        Set<String> existingKeys = new HashSet<>();
        Map<String, Double> existingDrpMap = new HashMap<>();
        if (existingData != null) {
            for (StRnflRmodePojo pojo : existingData) {
                String key = pojo.getSTCD() + "_" + pojo.getTM();
                existingKeys.add(key);
                existingDrpMap.put(key, pojo.getDRP());
            }
        }

        List<StRnflRmodePojo> insertList = new ArrayList<>();
        List<StRnflRmodePojo> updateList = new ArrayList<>();

        for (StRnflRmodePojo pojo : newData) {
            String key = pojo.getSTCD() + "_" + pojo.getTM();
            if (existingKeys.contains(key)) {
                // 已有记录，比较DRP是否有变化
                Double oldDrp = existingDrpMap.get(key);
                if (oldDrp != null && Math.abs(oldDrp - (pojo.getDRP() != null ? pojo.getDRP() : 0.0)) > 0.001) {
                    updateList.add(pojo);
                }
            } else {
                // 新记录
                insertList.add(pojo);
            }
        }

        writeLogTxtStr("type=" + type + " updateList=" + updateList.size() + " insertList=" + insertList.size(), logFile);

        // 先更新（批量模式）
        if (updateList.size() > 0) {
            org.apache.ibatis.session.SqlSession batchSession = swzzmodeSqlSessionFactory
                    .openSession(org.apache.ibatis.session.ExecutorType.BATCH);
            try {
                StRnflRmodeData batchMapper = batchSession.getMapper(StRnflRmodeData.class);
                for (StRnflRmodePojo pojo : updateList) {
                    batchMapper.updateDrp(pojo);
                }
                batchSession.flushStatements();
                batchSession.commit();
            } finally {
                batchSession.close();
            }
            writeLogTxtStr("type=" + type + " 更新完成 " + updateList.size() + " 条", logFile);
        }

        // 再插入
        if (insertList.size() > 0) {
            batchInsertRmode(insertList, logFile);
        }
    }

    /**
     * 批量插入RMODE数据
     */
    private void batchInsertRmode(List<StRnflRmodePojo> dataList, String logFile) {
        int total = dataList.size();
        int batchSize = 4000;
        int num = total / batchSize;
        if (total % batchSize != 0) {
            num += 1;
        }
        for (int i = 0; i < num; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min((i + 1) * batchSize, total);
            List<StRnflRmodePojo> subList = dataList.subList(fromIndex, toIndex);
            stRnflRmodeData.insertALL(subList);
        }
        writeLogTxtStr("批量插入RMODE完成，共 " + total + " 条", logFile);
    }

    /**
     * 插入RLIST记录
     */
    private void insertNwpRlist(String ybtm, int fpdr, int productId, String logFile) {
        StRnflRlistPojo pojo = new StRnflRlistPojo();
        pojo.setID(UUID.randomUUID().toString().replaceAll("-", ""));
        pojo.setNCFILE("nwp/prePolygonForecast?productId=" + productId);
        pojo.setTM(ybtm);
        pojo.setFPDR((double) fpdr);
        stRnflRlistData.insertOne(pojo);
        writeLogTxtStr("插入RLIST记录: ID=" + pojo.getID() + " TM=" + ybtm + " FPDR=" + fpdr, logFile);
    }

    // ==================== NWP面雨量预报同步 END ====================

    public static void main(String[] args) {
        String url = "http://www.shzxt.cn/shpma/ShpMaServ/GetyfwPicListV1?SToken=697D2E3D911028CB969AA7723DB569CA&t=1729476341205";
        List<String> param = Arrays.asList("SToken", "t", "productGuid");
        List<String> vaule = Arrays.asList("697D2E3D911028CB969AA7723DB569CA", "1729476341205", "yfw_014");
        String result = "";
        // try {
        // result = PostHttp(url,param,vaule);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        System.out.println(result);

        String str = "";
    }
}
