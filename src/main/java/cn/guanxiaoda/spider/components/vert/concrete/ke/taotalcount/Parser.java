package cn.guanxiaoda.spider.components.vert.concrete.ke.taotalcount;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import im.nll.data.extractor.Extractors;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static im.nll.data.extractor.Extractors.json;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "keCountParser")
public class Parser extends BaseProcessor {


    @Override
    public void doProcess(Task task) {
        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body -> JSON.parseObject(body).get("args"))
                .map(String::valueOf)
                .ifPresent(args -> {
                    Integer totalCount = Optional.ofNullable(Extractors.on(args).extract(json("$.total")).asString())
                            .map(Integer::parseInt)
                            .orElse(1000);

                    task.getCtx().put("parsed", totalCount);
                    task.setStage("parsed");
                });
    }
}
