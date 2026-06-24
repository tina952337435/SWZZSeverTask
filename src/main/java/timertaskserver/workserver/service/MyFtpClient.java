package timertaskserver.workserver.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class MyFtpClient {
    private static FTPClient client;

    public static FTPClient open(String host,Integer port,String username,String password) throws IOException{
        client = new FTPClient();
        //client.addProtocolCommandListener(new PrintCommandListener(System.out));
        client.connect(host, port);
        int replyCode = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)){//连接失败
            client.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();
        client.login(username, password);
        return client;
    }

    public static void close() throws IOException {
        client.logout();
        client.disconnect();
    }


}
