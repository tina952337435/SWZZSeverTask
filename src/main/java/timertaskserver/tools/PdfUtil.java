package timertaskserver.tools;//package gaoqiserver.tools;
//
//import com.aspose.words.Document;
//import com.aspose.words.License;
//import com.aspose.words.SaveFormat;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
///**
// * Word 转 Pdf 帮助类
// *
// * 备注:需要引入 aspose-words-15.8.0-jdk16.jar
// */
//public class PdfUtil {
//
//    private static boolean getLicense() {
//        boolean result = false;
//        try {
//            InputStream is = PdfUtil.class.getClassLoader().getResourceAsStream("license.xml");
//            License aposeLic = new License();
//            aposeLic.setLicense(is);
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//    /**
//     * @param wordPath 需要被转换的word全路径带文件名
//     * @param pdfPath  转换之后pdf的全路径带文件名
//     */
//    public static void doc2pdf(String wordPath, String pdfPath) {
//        // 验证License 若不验证则转化出的pdf文档会有水印产生
//        if (!getLicense()) {
//            return;
//        }
//        try {
//            long old = System.currentTimeMillis();
//            //新建一个pdf文档
//            File file = new File(pdfPath);
//            FileOutputStream os = new FileOutputStream(file);
//            //Address是将要被转化的word文档
//            Document doc = new Document(wordPath);
//            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
//            doc.save(os, SaveFormat.PDF);
//            long now = System.currentTimeMillis();
//            os.close();
//            //转化用时
//            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    /**
//     * @param wordPath 需要被转换的word全路径带文件名
//     * @param pdfPath 转换之后pdf的全路径带文件名
//     */
//    public static void doc2pdf2(String wordPath, String pdfPath) {
//        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
//            return;
//        }
//        try {
//            long old = System.currentTimeMillis();
//            File file = new File(pdfPath); //新建一个pdf文档
//            FileOutputStream os = new FileOutputStream(file);
//            Document doc = new Document(wordPath); //Address是将要被转化的word文档
//            doc.save(os, com.aspose.words.SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
//            long now = System.currentTimeMillis();
//            os.close();
//            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); //转化用时
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param excelPath 需要被转换的excel全路径带文件名
//     * @param pdfPath 转换之后pdf的全路径带文件名
//     */
////    public static void excel2pdf(String excelPath, String pdfPath) {
////        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
////            return;
////        }
////        try {
////            long old = System.currentTimeMillis();
////            Workbook wb = new Workbook(excelPath);// 原始excel路径
////            FileOutputStream fileOS = new FileOutputStream(new File(pdfPath));
////            wb.save(fileOS, com.aspose.cells.SaveFormat.PDF);
////            fileOS.close();
////            long now = System.currentTimeMillis();
////            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); //转化用时
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//}
