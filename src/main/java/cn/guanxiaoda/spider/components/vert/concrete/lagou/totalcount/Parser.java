package cn.guanxiaoda.spider.components.vert.concrete.lagou.totalcount;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import im.nll.data.extractor.Extractors;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static im.nll.data.extractor.Extractors.json;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "lagouCountParser")
public class Parser implements IProcessor<Task> {


    @Override
    public void process(Task task) {
        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body -> Extractors.on(body).extract(json("$.content.data.page.totalCount")).asString())
                .map(Integer::parseInt)
                .ifPresent(totalCount -> {
                    task.getCtx().put("parsed", totalCount);
                    task.setStage("parsed");
                });
    }
}
