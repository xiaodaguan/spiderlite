package cn.guanxiaoda.spider.components.vert.concrete.wx;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import im.nll.data.extractor.Extractors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static im.nll.data.extractor.Extractors.json;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "wxParser")
public class Parser implements IProcessor<Task> {
    @Override
    public void process(Task task) {
        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .ifPresent(body -> {
                    List result = Extractors.on(body).split(json("$..app_msg_list.*"))
                            .extract("title", json(".title"))
                            .extract("url", json(".link"))
                            .asMapList();
                    if (CollectionUtils.isEmpty(result)) {
                        task.getCtx().put("stopFlip", true);
                        return;
                    }

                    task.getCtx().put("parsed", result);
                    task.setStage("parsed");
                });
    }
}
