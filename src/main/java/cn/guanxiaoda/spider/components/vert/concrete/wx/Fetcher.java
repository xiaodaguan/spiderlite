package cn.guanxiaoda.spider.components.vert.concrete.wx;

import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.utils.RetryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "wxFetcher")
@Slf4j
public class Fetcher extends BaseFetcher {

    private static RateLimiter rl = RateLimiter.create(0.1);
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .put("Accept-Encoding", "gzip, deflate, br")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Cache-Control", "max-age=0")
                    .put("Connection", "keep-alive")
                    .put("Host", "mp.weixin.qq.com")
                    .put("Upgrade-Insecure-Requests", "1")
                    .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3409.0 Safari/537.36")
                    .build()
    );
    @Autowired ClientPool clientPool;

    @Override
    public void doProcess(Task task) {
        String token = Optional.of(task.getCtx()).map(ctx -> ctx.get("token")).map(String::valueOf).orElse("");
        String fakeId = Optional.of(task.getCtx()).map(ctx -> ctx.get("fakeId")).map(String::valueOf).orElse("");
        String cookies = Optional.of(task.getCtx()).map(ctx -> ctx.get("cookies")).map(String::valueOf).orElse("");
        int pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(Integer.class::cast).orElse(1);
        int pageSize = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageSize")).map(Integer.class::cast).orElse(10);
        String url = Optional.of(task.getCtx()).map(ctx -> ctx.get("url")).map(String::valueOf)
                .map(tmp -> tmp.replace("{begin}", String.valueOf((pageNo - 1) * pageSize)))
                .map(tmp -> tmp.replace("{token}", token))
                .map(tmp -> tmp.replace("{fakeId}", fakeId))
                .orElse("");

        Unirest.setHttpClient(clientPool.getApacheClient());
        rl.acquire();
        HttpResponse<String> response = RetryUtils.retry(() -> Unirest.get(url)
                .header("Cookie", cookies)
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
