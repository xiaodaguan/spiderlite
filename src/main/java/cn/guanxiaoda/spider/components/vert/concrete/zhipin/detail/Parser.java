package cn.guanxiaoda.spider.components.vert.concrete.zhipin.detail;

import cn.guanxiaoda.spider.components.vert.concrete.BaseSyncProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.Maps;
import im.nll.data.extractor.Extractors;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static im.nll.data.extractor.Extractors.selector;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "zhipinDetailParser")
public class Parser extends BaseSyncProcessor {


    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean doProcess(Task task) {
        Map<String, String> result = Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
//                .map(body -> body.replace("\\\"", "\""))
                .map(body ->
                        Optional.of(body)
                                .map(html ->
                                        Extractors.on(html)
                                                .extract("requirement", selector("div.job-banner>p.text"))
                                                .extract("tags", selector("div.job-banner>div.job-tags.text"))
                                                .extract("desc", selector("div.job-detail > div.detail-content > div.job-sec > div.text.text"))
                                                .extract("coordinate", selector("div.job-detail > div.detail-content > div.job-sec div#map-container.attr(data-long-lat)"))
                                                .asMap()
                                )
                                .map(item -> {
                                    item.put("crawlTime", LocalDateTime.now().format(dtf));
                                    item.put("city", String.valueOf(task.getCtx().get("city")));
                                    item.put("href", String.valueOf(task.getCtx().get("href")));
                                    item.put("title", String.valueOf(task.getCtx().get("title")));
                                    item.put("salary", String.valueOf(task.getCtx().get("salary")));
                                    item.put("uniqueKey", String.valueOf(task.getCtx().get("href")));
                                    item.put("companyName", String.valueOf(task.getCtx().get("companyName")));
                                    return item;
                                }).orElse(Maps.newHashMap())


                ).orElse(Maps.newHashMap());

        if (MapUtils.isEmpty(result)) {
            return false;
        }

        task.getCtx().put("parsed", result);
        task.setStage("parsed");
        return true;
    }
}
