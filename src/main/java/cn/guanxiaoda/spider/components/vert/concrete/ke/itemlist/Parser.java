package cn.guanxiaoda.spider.components.vert.concrete.ke.itemlist;

import cn.guanxiaoda.spider.components.vert.BaseProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.nll.data.extractor.Extractors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
@Component(value = "keParser")
public class Parser extends BaseProcessor {


    @Override
    public void doProcess(Task task) {
        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .ifPresent(body -> {
                    List result = Optional.of(Extractors.on(body).extract(json("$.body")).asString())
                            .filter(StringUtils::isNotBlank)
                            .map(html -> Extractors.on(html).split(selector("div.flexbox.html"))
                                    .extract("name", selector("div.item_list>div.item_main.text"))
                                    .extract("rawText", selector("div.item_list>div.item_other.text"))
                                    .extract("tags", selector("div.item_list>div.tag_box.text"))
                                    .extract("totalPrice", selector("div.item_list>div.item_minor>span.price_total>em.text"))
                                    .extract("unitPrice", selector("div.item_list>div.item_minor>span.unit_price.text"))
                                    .asMapList()
                                    .stream()
                                    .map(m -> Optional.ofNullable(m.get("rawText"))
                                            .map(text -> StringUtils.split(text, '/'))
                                            .filter(splited -> splited.length == 4)
                                            .map(splited -> {
                                                Map<String, String> newMap = Maps.newHashMap(m);
                                                newMap.put("houseType", splited[0]);
                                                newMap.put("area", splited[1]);
                                                newMap.put("orientation", splited[2]);
                                                newMap.put("community", splited[3]);
                                                return newMap;
                                            }).orElse(m)
                                    ).collect(Collectors.toList())
                            ).orElse(Lists.newArrayList(Maps.newHashMap()));

                    if (CollectionUtils.isEmpty(result)) {
                        task.getCtx().put("stopFlip", true);
                        return;
                    }

                    task.getCtx().put("parsed", result);
                    task.setStage("parsed");
                });
    }
}
