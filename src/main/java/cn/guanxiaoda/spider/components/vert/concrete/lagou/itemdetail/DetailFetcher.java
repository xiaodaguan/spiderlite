package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "lagouDetailFetcher")
@Slf4j
public class DetailFetcher extends BaseFetcher {

    private static final String URL_TEMPLATE = "https://m.lagou.com/jobs/{positionId}.html";

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
    @Autowired ClientPool clientPool;


    @Override
    public void fetch(Task task, ICallBack callBack) {
        String positionId = Optional.of(task.getCtx()).map(ctx -> ctx.get("positionId")).map(String::valueOf).orElse("");

        String url = URL_TEMPLATE.replace("{positionId}", positionId);

        handleRequest(task, url, headers, callBack);
    }
}
