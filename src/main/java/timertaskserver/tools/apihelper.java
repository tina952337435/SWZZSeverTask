package timertaskserver.tools;


import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;


/**
 * 这里键入类的描述
 *
 * @author 高起
 * @版权： 版权所有 (c) 2018
 * @see：
 * @创建日期： 2018/12/10 14:49
 * @功能说明：
 */
public class apihelper {
    /**
     * 发送post 请求访问本地应用并根据传递参数的不同返回不同的结果
     * @param url 访问地址
     * @param parmasMap 传递的参数
     */
    /**
     * 创建带超时的 HttpClient。
     * 默认的 HttpClients.createDefault() 无连接/读取超时，接口卡死时线程会无限阻塞，
     * 进而耗尽调度线程池。这里统一加上超时，失败快速返回。
     */
    private static CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30 * 1000)              // 建立连接超时 30 秒
                .setSocketTimeout(60 * 1000)               // 读取数据超时 60 秒（同步接口可能较慢）
                .setConnectionRequestTimeout(30 * 1000)    // 从连接池获取连接超时 30 秒
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static String apipost(String url, String parmasMap, HashMap<String, Object> header){
        String data = null;
        //创建带超时的httpClient实例
        CloseableHttpClient httpClient = createHttpClient();
        HttpPost httpPost = new HttpPost(url);
        if (header != null){
            header.forEach((key,value)->{
                httpPost.addHeader(key,value.toString());
            });
        }
        try{
            ColumnName param = new ColumnName();
            // 设置请求体（参数）
            StringEntity entity = new StringEntity(parmasMap, "UTF-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try{
                HttpEntity entityResult = response.getEntity();
                if (entityResult != null){
                    data = EntityUtils.toString(entityResult);
                    return data;
                }
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
    public static String apiget(String url, String parmasMap, HashMap<String, Object> header){
        String data = null;
        //创建带超时的httpClient实例
        CloseableHttpClient httpClient = createHttpClient();
        HttpGet httpGet = new HttpGet(url);
        if (header != null){
            header.forEach((key,value)->{
                httpGet.addHeader(key,value.toString());
            });
        }
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try{
                HttpEntity entityResult = response.getEntity();
                if (entityResult != null){
                    data = EntityUtils.toString(entityResult);
                    return data;
                }
                return data;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
}
