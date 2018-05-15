package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist;

import cn.guanxiaoda.spider.components.vert.BaseProcessor;
import cn.guanxiaoda.spider.models.Task;
import im.nll.data.extractor.Extractors;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

import static im.nll.data.extractor.Extractors.json;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "lagouListParser")
public class Parser extends BaseProcessor {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void doProcess(Task task) {
        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body ->
                        Extractors.on(body).split(json("$.content.data.page.result.*"))
                                .extract("positionId", json("positionId"))
                                .extract("positionName", json("positionName"))
                                .extract("city", json("city"))
                                .extract("createTime", json("createTime"))
                                .extract("salary", json("salary"))
                                .extract("companyId", json("companyId"))
                                .extract("companyLogo", json("companyLogo"))
                                .extract("companyName", json("companyName"))
                                .extract("companyFullName", json("companyFullName"))
                                .asMapList()
                                .stream()
                                .peek(item -> item.put("crawlTime", LocalDateTime.now().format(dtf)))
                                .peek(item -> item.put("uniqueKey", item.get("positionId")))
                                .collect(Collectors.toList())
                )
                .ifPresent(itemList -> {
                    task.getCtx().put("parsed", itemList);
                    task.setStage("parsed");
                });
    }
}
