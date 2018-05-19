package cn.guanxiaoda.spider.proxy;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/18
 */
@Slf4j
public abstract class BaseProxyManager implements IProxyManager {
    private static List<String> proxyContainer;
    private static CloseableHttpClient client = HttpClientBuilder.create().build();
    private static RateLimiter rl = RateLimiter.create(1);
    @Value("${proxy.vendor.url}")
    public String url;

    @Override
    public HttpHost randomGetOneHttpHost() {
        if (proxyContainer.size() == 0) {
            return null;
        }
        String proxy = proxyContainer.get(new Random().nextInt(proxyContainer.size()));
        log.info("[random proxy]: {}, pool size: {}", proxy, CollectionUtils.size(proxyContainer));
        return Optional.of(proxy)
                .map(str -> str.split(":"))
                .map(arr -> new HttpHost(arr[0], Integer.parseInt(arr[1])))
                .orElse(null);
    }

    @Override
    public SocketAddress randomGetOneAddress() {
        if (proxyContainer.size() == 0) {
            return null;
        }
        String proxy = proxyContainer.get(new Random().nextInt(proxyContainer.size()));
        log.info("[random proxy]: {}, pool size: {}", proxy, CollectionUtils.size(proxyContainer));
        return Optional.of(proxy)
                .map(str -> str.split(":"))
                .map(arr -> new InetSocketAddress(arr[0], Integer.parseInt(arr[1])))
                .orElse(null);
    }

    @Scheduled(fixedRate = 1500)
    public void refresh() {
        if (proxyContainer == null) {
            proxyContainer = Lists.newArrayList();
        }

        HttpGet get = new HttpGet(url);
        String content = null;
        try {
            rl.acquire();
            CloseableHttpResponse response = client.execute(get);
            if (200 != response.getStatusLine().getStatusCode()) {
                log.warn("didn't get proxy");
                return;
            }
            content = Optional.of(response)
                    .map(HttpResponse::getEntity)
                    .map(entity -> {
                        try {
                            return EntityUtils.toString(entity);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).orElse(null);
        } catch (Exception e) {
            log.error("refresh proxy failure.", e);
        }
        proxyContainer.addAll(getIpPortList(content));


        synchronized (this) {
            if (CollectionUtils.size(proxyContainer) > 20) {
                proxyContainer = proxyContainer.stream().skip(2).collect(Collectors.toList());
            }
        }
    }

    @Override
    public abstract List<String> getIpPortList(String content);

    @PostConstruct
    public void init() {
        refresh();
    }
}
