package cn.guanxiaoda.spider.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
public class ClientPool {
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();


    static {
        cm.setDefaultMaxPerRoute(200);
        cm.setMaxTotal(500);
    }

    public static CloseableHttpClient getDefaultClient() {

        return HttpClients.custom().setConnectionManager(cm).build();
    }
}
