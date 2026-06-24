package timertaskserver.tools;

import org.springframework.util.ResourceUtils;
//import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.rmi.runtime.Log;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lk
 * @Date 2020-08-31 16:27
 * @Description 文件路径替换
 */
public class FilePathUtils {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
//    public static final String FILE_SEPARATOR api/jsszy/viewWaterQ= File.separator;


    public static String getRealFilePath(String path) {
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }

    public static String getHttpURLPath(String path) {
        return path.replace("\\", "/");
    }

    public static Boolean copyNio(String from, String to) {
        FileChannel input = null;
        FileChannel output = null;

        try {
            input = new FileInputStream(new File(from)).getChannel();
            output = new FileOutputStream(new File(to)).getChannel();
            output.transferFrom(input, 0, input.size());
            input.close();
            output.close();
        } catch (Exception e) {
            return false;
        } finally {
            return true;
        }
    }

    /**
     * base64转图片
     * @param base64str base64码
     * @param savePath 图片路径
     * @return
     */
    public static boolean GenerateImage(String base64str, String savePath) {
        //对字节数组字符串进行Base64解码并生成图片
        if (base64str == null) {
            return false;
        }
//        BASE64Decoder decoder = new BASE64Decoder();
        Base64 base64encoder = new Base64();
//        return base64encoder.encodeBase64String(xxx);

        try {
            //Base64解码
//            byte[] b = decoder.decodeBuffer(base64str);
            byte[] b = base64encoder.decodeBase64(base64str);
            for (int i = 0; i < b.length; ++i) {
                //调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(savePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * base64转图片
     * @param base64Code base64码
     */
    public static void convertBase64ToImage(String base64Code, String savePath){
        BufferedImage image = null;
        byte[] imageByte = null;
        try {
            imageByte = DatatypeConverter.parseBase64Binary(base64Code);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(new ByteArrayInputStream(imageByte));
            bis.close();
            File outputfile = new File(savePath);
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<File> getAllFiles(String folderPath) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return fileList;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    fileList.addAll(getAllFiles(file.getAbsolutePath()));
                }
            }
        }
        return fileList;
    }

    public static void deleteAllFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteAllFiles(file); // 递归处理子目录
                } else {
                    if (file.delete()) {
                        System.out.println("已删除: " + file.getAbsolutePath());
                    } else {
                        System.out.println("删除失败: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }
}
