package cn.guanxiaoda.spider.http;

import cn.guanxiaoda.spider.proxy.IProxyManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    CloseableHttpClient getApacheClient() {
        RequestConfig conf = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
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
                .cookieJar(CookieJar.NO_COOKIES)
                .connectionPool(okPool);

        Optional.ofNullable(proxyManager)
                .map(IProxyManager::randomGetOneAddress)
                .map(addr -> new Proxy(Proxy.Type.HTTP, addr))
                .ifPresent(builder::proxy);

        return builder.build();
    }

    public OkHttpClient getOkClientNoPoolingWithCookie() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .followRedirects(false)
                .followSslRedirects(false)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    //Tip：這裡key必須是String
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }

                });

        Optional.ofNullable(proxyManager)
                .map(IProxyManager::randomGetOneAddress)
                .map(addr -> new Proxy(Proxy.Type.HTTP, addr))
                .ifPresent(builder::proxy);
        return builder.build();

    }

}
