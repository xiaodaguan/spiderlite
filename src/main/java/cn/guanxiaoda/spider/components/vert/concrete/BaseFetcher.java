package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
    @Value("${fetch.ratelimit}") private String rlStr;

    @PostConstruct
    public void init() {
        rlMap = Maps.newConcurrentMap();
        rlMap.putAll(
                JSON.parseObject(Optional.ofNullable(rlStr).orElse(""), new TypeReference<Map<String, Double>>() {})
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> RateLimiter.create(entry.getValue())))
        );
    }

    protected RateLimiter getRatelimiter(String fetcherName) {
        return rlMap.getOrDefault(fetcherName, RateLimiter.create(0.1));
    }

    @Override
    public abstract void doProcess(Task task, ICallBack callBack);

    protected void handleRequest(Task task, String url, Map<String, String> headers, ICallBack callBack) {
        clientPool.getOkClient()
                .newCall(new Request.Builder()
                        .headers(Headers.of(headers))
                        .url(url)
                        .build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        log.error("http client call failure", e);
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
