package cn.guanxiaoda.spider.components.vert.concrete.ke.taotalcount;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("keCountStarter")
public class Starter extends BaseProcessor {
    @Override
    public void doProcess(Task task) {
        String cityId = Optional.of(task.getCtx()).map(ctx -> ctx.get("cityId")).map(String::valueOf).orElse("");
        task.getCtx().putAll(
                Maps.newHashMap(ImmutableMap.<String, Object>builder()
                        .put("url", "https://m.ke.com/{cityId}/ershoufang/pg{pageNo}/?_t=1")
                        .put("cityId", cityId)
                        .put("pageNo", 1)
                        .put("pageSize", 30)
                        .put("collection", "beikezhaofang_count")
                        .build()
                )
        );
        task.setStage("init");
    }
}
