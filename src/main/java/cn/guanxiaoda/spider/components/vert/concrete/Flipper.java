package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.conf.FastJsonConf;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
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
@Component("commonFlipper")
@Slf4j
public class Flipper extends BaseSyncProcessor {
    @Override
    public boolean doProcess(Task task) {
        List<Map<String, Object>> list = Optional.ofNullable(task.getCtx())
                .map(t -> t.get("parsed"))
                .map(parsed -> (List<Map<String, Object>>) parsed)
                .orElse(Lists.newArrayList());
        if (CollectionUtils.isEmpty(list)) {
            log.info("no following pages, task={}", JSON.toJSONString(task, FastJsonConf.filter));
            task.setStage("stopped");
            return false;
        }

        Integer maxPage = Optional.ofNullable(task.getCtx().get("maxPage")).map(Integer.class::cast).orElse(Integer.MAX_VALUE);
        Integer curPage = Optional.ofNullable(task.getCtx().get("pageNo")).map(Integer.class::cast).orElse(1);
        if (curPage >= maxPage) {
            task.setStage("stopped");
            log.info("reach max pages, will stop, task={}", JSON.toJSONString(task, FastJsonConf.filter));
            return false;
        }
        task.getCtx().put("pageNo", Optional.of(task.getCtx())
                .map(ctx -> ctx.get("pageNo"))
                .map(Integer.class::cast)
                .map(pageNo -> pageNo + 1)
                .get()
        );
        log.info("will crawl following pages, task={}", JSON.toJSONString(task, FastJsonConf.filter));
        task.setStage("paged");
        task.setRetryNo(0);
        return true;
    }
}
