package cn.guanxiaoda.spider.http;

import cn.guanxiaoda.spider.proxy.IProxyManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InterruptedIOException;
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Optional.ofNullable(proxyManager)
                .map(IProxyManager::randomGetOneAddress)
                .map(addr -> new Proxy(Proxy.Type.HTTP, addr))
                .ifPresent(builder::proxy);

        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectionPool(okPool);

        /* retry */
        builder.addInterceptor(chain -> {
            Request request = chain.request();
            Response response = doRequest(chain, request);
            int retryNum = 0;
            while ((response == null || !response.isSuccessful()) && retryNum <= 5) {
                log.info("intercept Request is not successful - {}", retryNum);
                final long nextInterval = 1000L;
                try {
                    log.info("Wait for {}", nextInterval);
                    Thread.sleep(nextInterval);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                }
                retryNum++;
                // retry the request
                response = doRequest(chain, request);
            }
            return response;
        });

        return builder.build();
    }

    private Response doRequest(Interceptor.Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log.error("doRequest failure", e);
        }
        return response;
    }
}
