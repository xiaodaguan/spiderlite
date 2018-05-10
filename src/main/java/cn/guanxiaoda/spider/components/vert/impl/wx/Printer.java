package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "wxPrinter")
@Slf4j
public class Printer implements IProcessor<Task> {
    @Override
    public Task process(Task task) {
        List parsed = Optional.ofNullable(task.getCtx()).map(ctx -> ctx.get("parsed"))
                .map(obj -> (List<Map<String, Object>>) obj)
                .orElse(Lists.newArrayList());
        log.info("item crawled: {}", JSON.toJSONString(parsed));
        task.setStage("printed");
        return task;
    }
}
