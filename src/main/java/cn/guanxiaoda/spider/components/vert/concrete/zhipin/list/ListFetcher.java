package cn.guanxiaoda.spider.components.vert.concrete.zhipin.list;

import cn.guanxiaoda.spider.annotation.Processor;
import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "zhipinListFetcher")
@Slf4j
public class ListFetcher extends BaseFetcher {


    private static final String URL_TEMPLATE = "https://m.zhipin.com/mobile/jobs.json?page={pageNo}&city={city}&query={keyword}";
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()

                    .put("accept", "*/*")
                    .put("accept-language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("referer", "https://m.zhipin.com/job_detail/?query=java&scity=101010100&industry=&position=")
                    .put("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3426.2 Mobile Safari/537.36")
                    .put("x-requested-with", "XMLHttpRequest")
                    .build()
    );


    @Override
    public void fetch(Task task, ICallBack callBack) {
        String city = Optional.of(task.getCtx()).map(ctx -> ctx.get("city")).map(String::valueOf).map(URLEncoder::encode).orElse("");
        String keyword = Optional.of(task.getCtx()).map(ctx -> ctx.get("keyword")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(String::valueOf).map(Integer::parseInt).orElse(1);

        String url = URL_TEMPLATE.replace("{keyword}", keyword).replace("{city}", city).replace("{pageNo}", String.valueOf(pageNo));

        handleRequest(task, url, headers, callBack);
    }
}
