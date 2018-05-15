package cn.guanxiaoda.spider.components.vert.concrete.lagou;

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

import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "lagouListFetcher")
@Slf4j
public class ListFetcher implements IProcessor<Task> {

    private static final String URL_TEMPLATE = "https://m.lagou.com/search.json?city={city}&positionName={positionName}&pageNo={pageNo}&pageSize=15";
    private static RateLimiter rl = RateLimiter.create(0.1);
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()

                    .put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .put("Accept-Encoding", "gzip, deflate, br")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Cache-Control", "max-age=0")
//                    .put("Connection", "keep-alive")
                    .put("Host", "m.lagou.com")
                    .put("Upgrade-Insecure-Requests", "1")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3426.2 Mobile Safari/537.36")

                    .build()
    );
    @Autowired @Qualifier("gobanjiaProxyManager") private IProxyManager proxyManager;

    @Override
    public void process(Task task) {
        String city = Optional.of(task.getCtx()).map(ctx -> ctx.get("city")).map(String::valueOf).map(URLEncoder::encode).orElse("");
        String positionName = Optional.of(task.getCtx()).map(ctx -> ctx.get("positionName")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(String::valueOf).map(Integer::parseInt).orElse(1);

        String url = URL_TEMPLATE.replace("{positionName}", positionName).replace("{city}", city).replace("{pageNo}", String.valueOf(pageNo));

        Unirest.setHttpClient(ClientPool.getDefaultClient());
        rl.acquire();
        Optional.ofNullable(proxyManager.randomGetOne()).ifPresent(Unirest::setProxy);
        HttpResponse<String> response = RetryUtils.retry(() -> Unirest.get(url).headers(headers).asString());

        Optional.ofNullable(response)
                .ifPresent(resp -> {
                    if (resp.getStatus() != HttpStatus.SC_OK) {
                        return;
                    }
                    task.getCtx().put("fetched", response.getBody());
                    task.setStage("fetched");
                });

    }

}
