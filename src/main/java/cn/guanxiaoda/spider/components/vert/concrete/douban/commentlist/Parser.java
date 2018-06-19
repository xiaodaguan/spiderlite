package cn.guanxiaoda.spider.components.vert.concrete.douban.commentlist;

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
import static im.nll.data.extractor.Extractors.selector;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "doubanCommentListParser")
public class Parser extends BaseSyncProcessor {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean doProcess(Task task) {
        List<Map<String, String>> result = Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body ->
                        Extractors.on(body).split(json("$.interests.*"))
                                .extract("commentId", json("$.id"))
                                .extract("comment", json("$.comment"))
                                .extract("score", json("$.rating.value"))
                                .extract("create_time", json("$.create_time"))
                                .extract("user_name", json("$.user.name"))
                                .extract("user_id", json("$.user.uid"))
                                .asMapList()
                                .stream()
                                .peek(item -> item.put("crawlTime", LocalDateTime.now().format(dtf)))
                                .peek(item -> item.put("uniqueKey", item.get("commentId")))
                                .peek(item -> item.put("movieId", String.valueOf(task.getCtx().get("movieId"))))
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
