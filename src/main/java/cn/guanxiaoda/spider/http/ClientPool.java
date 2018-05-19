package cn.guanxiaoda.spider.http;

import cn.guanxiaoda.spider.proxy.IProxyManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component
@Slf4j
public class ClientPool {
    private static ConnectionPool okPool = new ConnectionPool();
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    static {
        cm.setDefaultMaxPerRoute(200);
        cm.setMaxTotal(500);


    }

    @Autowired @Qualifier("gobanjiaProxyManager") private IProxyManager proxyManager;

    public CloseableHttpClient getApacheClient() {
        RequestConfig conf = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setProxy(proxyManager.randomGetOneHttpHost())
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(conf)
                .setConnectionManager(cm)
                .disableCookieManagement()
                .build();
    }

    public OkHttpClient getOkClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .followRedirects(false)
                .followSslRedirects(false)
                .connectionPool(okPool);

        Optional.ofNullable(proxyManager)
                .map(IProxyManager::randomGetOneAddress)
                .map(addr -> new Proxy(Proxy.Type.HTTP, addr))
                .ifPresent(builder::proxy);

        return builder.build();
    }

}
