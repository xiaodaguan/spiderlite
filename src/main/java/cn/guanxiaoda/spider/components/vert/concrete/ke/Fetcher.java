package cn.guanxiaoda.spider.components.vert.concrete.ke;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.proxy.IProxyManager;
import cn.guanxiaoda.spider.utils.RetryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "keFetcher")
@Slf4j
public class Fetcher implements IProcessor<Task> {

    private static RateLimiter rl = RateLimiter.create(0.05);
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "application/json")
                    .put("Accept-Encoding", "gzip, deflate, br")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Connection", "keep-alive")
                    .put("Host", "m.ke.com")
                    .put("Referer", "https://m.ke.com/bj/ershoufang/pg2/")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3426.2 Mobile Safari/537.36")
                    .put("X-Requested-With", "XMLHttpRequest")
                    .build()
    );
    @Autowired @Qualifier("gobanjiaProxyManager") private IProxyManager proxyManager;

    @Override
    public void process(Task task) {
        String cityId = Optional.of(task.getCtx()).map(ctx -> ctx.get("cityId")).map(String::valueOf).orElse("");
        int pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(Integer.class::cast).orElse(1);
        String url = Optional.of(task.getCtx()).map(ctx -> ctx.get("url")).map(String::valueOf)
                .map(tmp -> tmp.replace("{pageNo}", String.valueOf(pageNo)))
                .map(tmp -> tmp.replace("{cityId}", cityId))
                .orElse("");

        Unirest.setHttpClient(ClientPool.getDefaultClient());
        rl.acquire();
        Optional.ofNullable(proxyManager.randomGetOne()).ifPresent(Unirest::setProxy);
        HttpResponse<String> response = RetryUtils.retry(() -> Unirest.get(url)
                .headers(headers)
                .asString()
        );

        Optional.ofNullable(response).ifPresent(resp -> {
            if (resp.getStatus() != HttpStatus.SC_OK) {
                return;
            }
            task.getCtx().put("fetched", response.getBody());
            task.setStage("fetched");
        });
    }

}
