package cn.guanxiaoda.spider.proxy;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/18
 */
@Slf4j
public abstract class BaseProxyManager implements IProxyManager {
    private static final int PROXY_FAIL_MAX = 2;
    private static final int PROXY_POOL_SIZE = 6;
    private static final int REFRESH_INTERVAL = 1500;
    private static List<String> proxyContainer;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3000, TimeUnit.MILLISECONDS)
            .readTimeout(3000, TimeUnit.MILLISECONDS)
            .writeTimeout(3000, TimeUnit.MILLISECONDS)
            .build();
    private static RateLimiter rl = RateLimiter.create(1);
    @Value("${proxy.vendor.url}")
    public String url;
    private Map<String, Integer> proxyFailTimeMap = new ConcurrentHashMap<>();

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

    @Scheduled(fixedRate = REFRESH_INTERVAL)
    public void refresh() {
        if (proxyContainer == null) {
            proxyContainer = Lists.newArrayList();
        }

        rl.acquire();
        client.newCall(new Request.Builder().get().url(url).build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        log.error("fetch proxy failure, msg={}", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        Optional.of(response)
                                .map(Response::body)
                                .ifPresent(responseBody -> {
                                    try {
                                        proxyContainer.addAll(getIpPortList(responseBody.string()));
                                    } catch (IOException e) {
                                        log.error("get response body string failure, msg={}", e.getMessage());
                                    }
                                });
                    }
                });


        synchronized (this) {
            if (CollectionUtils.size(proxyContainer) > PROXY_POOL_SIZE) {
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

    @Override
    public synchronized void removeProxy(String ipPort) {
        if (Strings.isNullOrEmpty(ipPort)) {
            return;
        }
        log.info("remove proxy from pool: {}", ipPort);
        proxyContainer = proxyContainer.stream().filter(s -> !ipPort.equals(s)).collect(Collectors.toList());
    }

    @Override
    public synchronized void recordProxyFailure(String ipPort) {
        proxyFailTimeMap.put(ipPort, proxyFailTimeMap.getOrDefault(ipPort, 0) + 1);
        if (proxyFailTimeMap.get(ipPort) > PROXY_FAIL_MAX) {
            removeProxy(ipPort);
        }
    }
}
