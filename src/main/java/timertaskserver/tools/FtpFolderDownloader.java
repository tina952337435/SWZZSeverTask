package timertaskserver.tools;
import org.apache.commons.net.ftp.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;

public class FtpFolderDownloader {
    private static final int BUFFER_SIZE = 1024 * 1024; // 1MB缓冲
    private static final int RETRY_INTERVAL = 30; // 秒
    private final FTPClient ftp = new FTPClient();
    private final Set<String> downloadedFiles = ConcurrentHashMap.newKeySet();
    private final String remoteDir;
    private final String localDir;

    public FtpFolderDownloader(String host, int port, String user, String password, String remoteDir, String localDir) throws IOException {
        this.remoteDir = remoteDir;
        this.localDir = localDir;

        // 连接配置
        ftp.connect(host, port);
        ftp.login(user, password);
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.setBufferSize(BUFFER_SIZE);
        ftp.setDataTimeout(300000); // 5分钟超时
    }

    public void startMonitoring(long interval) {
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(this::downloadNewFiles, 0, interval, TimeUnit.SECONDS);

        downloadNewFiles();
    }

    private void downloadNewFiles() {
        try {
            FTPFile[] files = ftp.listFiles(remoteDir);
            for (FTPFile file : files) {
                if (file.isFile() && !downloadedFiles.contains(file.getName())) {
                    downloadFile(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(FTPFile file) throws IOException {
        String remotePath = remoteDir + "/" + file.getName();
        String localPath = localDir + "/" + file.getName();
        File localFile = new File(localPath);

        // 断点续传逻辑
        long localSize = localFile.exists() ? localFile.length() : 0;
        if (localSize < file.getSize()) {
            try (OutputStream output = new FileOutputStream(localFile, localSize > 0)) {
                ftp.setRestartOffset(localSize);
                if (!ftp.retrieveFile(remotePath, output)) {
                    System.out.println("Transfer failed: " + ftp.getReplyString());
                }
                verifyFileSize(file, localPath);
            }
        }
    }

    private void verifyFileSize(FTPFile file, String localPath) throws IOException {
        if (Files.size(Paths.get(localPath)) != file.getSize()) {
            System.out.println("File size mismatch after download，本地大小："+Files.size(Paths.get(localPath))+"，ftp文件大小："+file.getSize()+"***************************"+file.getName());

        }else{
            downloadedFiles.add(file.getName());
        }
    }

    public void close() throws IOException {
        if (ftp != null && ftp.isConnected()) {
            ftp.logout();
            ftp.disconnect();
        }
    }
}
