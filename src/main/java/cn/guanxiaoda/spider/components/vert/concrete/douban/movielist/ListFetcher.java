package cn.guanxiaoda.spider.components.vert.concrete.douban.movielist;

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
@Component(value = "doubanMovieListFetcher")
@Slf4j
public class ListFetcher extends BaseFetcher {


    private static final String URL_TEMPLATE = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags={tag}&start={start}";
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "application/json, text/plain, */*")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Connection", "keep-alive")
                    .put("Host", "movie.douban.com")
                    .put("Referer", "https://movie.douban.com/tag/")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36")
                    .build()
    );


    @Override
    public void fetch(Task task, ICallBack callBack) {
        String tag = Optional.of(task.getCtx()).map(ctx -> ctx.get("tag")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(String::valueOf).map(Integer::parseInt).orElse(1);
        String start = String.valueOf((pageNo - 1) * 20);
        String url = URL_TEMPLATE.replace("{start}", start).replace("{tag}",tag);

        handleRequest(task, url, headers, callBack);
    }
}
