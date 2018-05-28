package cn.guanxiaoda.spider.components.vert.concrete.zhipin.company;

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
@Component(value = "zhipinCompParser")
public class Parser extends BaseSyncProcessor {


    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean doProcess(Task task) {
        Map<String, String> result = Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body ->
                        Optional.of(body)
                                .map(html ->
                                        Extractors.on(html)
                                                //行业
                                                .extract("field", selector("div.company-info > div.company-primary > p:nth-child(3) > span:first-child"))
                                                //规模
                                                .extract("scale", selector("div.company-info > div.company-primary > p:nth-child(3) > span:last-child"))
                                                .asMap()
                                )
                                .map(item -> {
                                    item.put("crawlTime", LocalDateTime.now().format(dtf));
                                    item.put("city", String.valueOf(task.getCtx().get("city")));
                                    item.put("compHref", String.valueOf(task.getCtx().get("compHref")));
                                    item.put("coordinate", String.valueOf(task.getCtx().get("coordinate")));
                                    item.put("companyName", String.valueOf(task.getCtx().get("companyName")));
                                    item.put("uniqueKey", String.valueOf(task.getCtx().get("compHref")));
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
