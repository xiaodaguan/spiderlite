package cn.guanxiaoda.spider.components.vert.concrete.lagou;

import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.utils.RetryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Request;
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
public class ListFetcher extends BaseFetcher {


    private static final String URL_TEMPLATE = "https://m.lagou.com/search.json?city={city}&positionName={positionName}&pageNo={pageNo}&pageSize=15";
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()

                    .put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .put("Accept-Encoding", "gzip, deflate, br")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Cache-Control", "max-age=0")
//                    .put("Connection", "keep-alive")
                    .put("Host", "m.lagou.com")
                    .put("Upgrade-Insecure-Requests", "1")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3426.2 Mobile Safari/537.36")

                    .build()
    );

    @Override
    public void doProcess(Task task) {
        String city = Optional.of(task.getCtx()).map(ctx -> ctx.get("city")).map(String::valueOf).map(URLEncoder::encode).orElse("");
        String positionName = Optional.of(task.getCtx()).map(ctx -> ctx.get("positionName")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(String::valueOf).map(Integer::parseInt).orElse(1);

        String url = URL_TEMPLATE.replace("{positionName}", positionName).replace("{city}", city).replace("{pageNo}", String.valueOf(pageNo));

        rl.acquire();

        String response = RetryUtils.retry(() ->
                clientPool.getOkClient()
                        .newCall(new Request.Builder()
                                .headers(Headers.of(headers))
                                .url(url)
                                .build())
                        .execute()
                        .body()
                        .string());

        Optional.ofNullable(response)
                .ifPresent(resp -> {
                    task.getCtx().put("fetched", resp);
                    task.setStage("fetched");
                });
    }

}
