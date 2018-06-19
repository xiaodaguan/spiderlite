package cn.guanxiaoda.spider.components.vert.concrete.douban.movielist;

import cn.guanxiaoda.spider.components.vert.concrete.BaseSyncProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.Lists;
import im.nll.data.extractor.Extractors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static im.nll.data.extractor.Extractors.json;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "doubanMovieListParser")
public class Parser extends BaseSyncProcessor {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean doProcess(Task task) {
        List<Map<String, String>> result = Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body ->
                        Extractors.on(body).split(json("$.data.*"))
                                .extract("movieId", json("$.id"))
                                .extract("score", json("$.rate"))
                                .extract("directors", json("$.directors"))
                                .extract("casts", json("$.casts"))
                                .asMapList()
                                .stream()
                                .peek(item -> item.put("crawlTime", LocalDateTime.now().format(dtf)))
                                .peek(item -> item.put("uniqueKey", item.get("movieId")))
                                .collect(Collectors.toList())
                ).orElse(Lists.newArrayList());

        if (CollectionUtils.isEmpty(result)) {
            return false;
        }

        task.getCtx().put("parsed", result);
        task.setStage("parsed");
        return true;
    }
}
