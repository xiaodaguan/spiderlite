package cn.guanxiaoda.spider.components.vert.concrete.douban.commentlist;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.concrete.BaseFetcher;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "doubanCommentListFetcher")
@Slf4j
public class ListFetcher extends BaseFetcher {


    private static final String URL_TEMPLATE = "https://m.douban.com/rexxar/api/v2/movie/{movieId}/interests?count=20&order_by=hot&start={start}&ck=&for_mobile=1";
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "application/json")
                    .put("Connection", "keep-alive")
                    .put("Host", "m.douban.com")
                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3443.0 Mobile Safari/537.36")
                    .put("X-Requested-With", "XMLHttpRequest")
                    .build()
    );


    @Override
    public void fetch(Task task, ICallBack callBack) {
        String movieId = Optional.of(task.getCtx()).map(ctx -> ctx.get("movieId")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(String::valueOf).map(Integer::parseInt).orElse(1);
        String start = String.valueOf((pageNo - 1) * 25);
        headers.put("Referer", String.format("https://m.douban.com/movie/subject/%s/comments?sort=new_score&start=%s", movieId, start));

        String url = URL_TEMPLATE.replace("{movieId}", movieId).replace("{start}", start);

        handleRequest(task, url, headers, callBack);
//        OkHttpClient client = clientPool.getOkClientNoPoolingWithCookie();
//        try {
//            client.newCall(new Request.Builder()
//                    .headers(Headers.of(headers))
//                    .url("https://m.douban.com/")
//                    .build()
//            ).execute();
//        } catch (IOException e) {
//            log.error("douban list fetcher execute homepage failure, url={}", url, e);
//            return;
//        }
//        String bodyString;
//        try {
//            bodyString = client.newCall(new Request.Builder()
//                    .headers(Headers.of(headers))
//                    .url(url)
//                    .build()
//            ).execute().body().string();
//        } catch (IOException e) {
//            log.error("douban list fetcher execute comments failure, url={}", url, e);
//            return;
//        }
//
//        if (Strings.isNullOrEmpty(bodyString)) {
//            return;
//        }
//
//        task.getCtx().put("fetched", bodyString);
//        callBack.call(task);
    }
}
