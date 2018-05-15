package cn.guanxiaoda.spider.components.vert.concrete.wx;

import cn.guanxiaoda.spider.components.vert.BaseProcessor;
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
public class Pager extends BaseProcessor {
    @Override
    public void doProcess(Task task) {
        Optional.ofNullable(task.getCtx().get("parsed"))
                .map(obj -> (List<Map<String, Object>>) obj)
                .ifPresent(list -> {
                    if (CollectionUtils.isEmpty(list)) {
                        task.getCtx().put("stopFlip", true);
                        log.info("no following pages, task={}", JSON.toJSONString(task));
                        task.setStage("stopped");
                        return;
                    }

                    Integer maxPage = Optional.ofNullable(task.getCtx().get("maxPage")).map(Integer.class::cast).orElse(Integer.MAX_VALUE);
                    Integer curPage = Optional.ofNullable(task.getCtx().get("pageNo")).map(Integer.class::cast).orElse(1);
                    if (curPage >= maxPage) {
                        task.getCtx().put("stopFlip", true);
                        task.setStage("stopped");
                        log.info("reach max pages, will stop, task={}", JSON.toJSONString(task));
                        return;
                    }
                    task.getCtx().put("pageNo", Optional.of(task.getCtx())
                            .map(ctx -> ctx.get("pageNo"))
                            .map(Integer.class::cast)
                            .map(pageNo -> pageNo + 1)
                            .get()
                    );
                    task.getCtx().remove("fetched");
                    task.getCtx().remove("parsed");
                    log.info("will crawl following pages, task={}", JSON.toJSONString(task));
                    task.setStage("paged");
                });
    }
}
