package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("wxPager")
@Slf4j
public class Pager implements IProcessor<Task> {
    @Override
    public Task process(Task task) {
        Optional.of(task.getCtx().get("parsed"))
                .map(obj -> (List<Map<String, Object>>) obj)
                .ifPresent(list -> {
                    if (CollectionUtils.isEmpty(list)) {
                        task.getCtx().put("stopSend", true);
                        log.info("no following pages, task={}", JSON.toJSONString(task));
                        return;
                    }
                    task.getCtx().put("pageNo", Optional.of(task.getCtx())
                            .map(ctx -> ctx.get("pageNo"))
                            .map(Integer.class::cast)
                            .map(pageNo -> pageNo + 1)
                            .get()
                    );
                    log.info("will crawl following pages, task={}", JSON.toJSONString(task));

                });
        return null;
    }
}
