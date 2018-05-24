package cn.guanxiaoda.spider.components.vert.concrete.ke;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "keFetcher")
@Slf4j
public class Fetcher extends BaseFetcher {

    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "application/json")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Connection", "keep-alive")
                    .put("Host", "m.ke.com")
                    .put("Referer", "https://m.ke.com/bj/ershoufang/pg2/")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3426.2 Mobile Safari/537.36")
                    .put("X-Requested-With", "XMLHttpRequest")
                    .build()
    );


    @Override
    public void fetch(Task task, ICallBack callBack) {
        String cityId = Optional.of(task.getCtx()).map(ctx -> ctx.get("cityId")).map(String::valueOf).orElse("");
        int pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(Integer.class::cast).orElse(1);
        String url = Optional.of(task.getCtx()).map(ctx -> ctx.get("url")).map(String::valueOf)
                .map(tmp -> tmp.replace("{pageNo}", String.valueOf(pageNo)))
                .map(tmp -> tmp.replace("{cityId}", cityId))
                .orElse("");

        handleRequest(task, url, headers, callBack);
    }
}
