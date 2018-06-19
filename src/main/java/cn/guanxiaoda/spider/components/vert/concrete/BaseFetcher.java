package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.proxy.IProxyManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseFetcher extends BaseAsyncProcessor {

    /**
     * key: fetcherName, e.g. lagouListFetcher
     */
    private static Map<String, RateLimiter> rlMap = new ConcurrentHashMap<>();
    protected @Autowired ClientPool clientPool;
    @Autowired @Qualifier("gobanjiaProxyManager") protected IProxyManager proxyManager;
    @Value("${fetch.ratelimit}") private String rlStr;

    @PostConstruct
    public void init() {
        rlMap = Maps.newConcurrentMap();
        refresh();
    }

    @Scheduled(fixedRate = 1000)
    protected void refresh() {
        rlMap.clear();
        rlMap.putAll(
                JSON.parseObject(Optional.ofNullable(rlStr).orElse(""), new TypeReference<Map<String, Double>>() {})
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> RateLimiter.create(entry.getValue())))
        );
    }

    protected RateLimiter getRatelimiter(String hostName) {
        return rlMap.getOrDefault(hostName, RateLimiter.create(0.1));
    }

    @Override
    public void doProcess(Task task, ICallBack callBack) {
        fetch(task, callBack);
    }

    public abstract void fetch(Task task, ICallBack callBack);

    protected void handleRequest(Task task, String url, Map<String, String> headers, ICallBack callBack) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            log.error("invalid url: {}", url);
            return;
        }
        RateLimiter rl = getRatelimiter(Optional.of(httpUrl).map(HttpUrl::topPrivateDomain).orElse("default.rate.limiter"));
        rl.acquire();
        OkHttpClient client = clientPool.getOkClient();
        client.newCall(new Request.Builder()
                .headers(Headers.of(headers))
                .url(httpUrl)
                .build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
                            log.error("conn timeout: {}", e.getMessage());
                        } else if (e instanceof SocketException && Optional.ofNullable(e).map(IOException::getCause)
                                .map(Throwable::getMessage).orElse("").contains("Connection reset")) {
                            log.error("conn reset: {}", e.getMessage());
                        } else if (e instanceof ConnectException && Optional.ofNullable(e).map(IOException::getCause)
                                .map(Throwable::getMessage).orElse("").contains("Connection refused")) {
                            String ipPort = Optional.ofNullable(client.proxy())
                                    .map(Proxy::address)
                                    .map(String::valueOf)
                                    .map(str -> str.replace("/", ""))
                                    .orElse(null);
                            log.error("conn refused: ipPort={}, msg={}", ipPort, e.getMessage());
                            proxyManager.removeProxy(ipPort);
                        } else {
                            log.error("http client call failure", e);
                        }
                        retry(task);
                        proxyManager.recordProxyFailure(Optional.ofNullable(client.proxy())
                                .map(Proxy::address)
                                .map(String::valueOf)
                                .map(str -> str.replace("/", ""))
                                .orElse(null));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Optional.ofNullable(response.body())
                                .map(body -> {
                                    try {
                                        return body.string();
                                    } catch (IOException e) {
                                        log.error("get body string failure", e);
                                        return null;
                                    }
                                })
                                .ifPresent(content -> {
                                    if (StringUtils.isBlank(content)) {
                                        return;
                                    }
                                    task.setStage("fetched");
                                    task.getCtx().put("fetched", content);
                                    callBack.call(task);
                                });

                    }
                });

    }
}
