package cn.guanxiaoda.spider.components.vert.impl.ke.itemlist;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("keStarter")
public class Starter implements IProcessor<Task> {
    @Override
    public void process(Task task) {
        String cityId = Optional.of(task.getCtx()).map(ctx -> ctx.get("cityId")).map(String::valueOf).orElse("");
        Integer pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(Integer.class::cast).get();
        task.getCtx().putAll(
                Maps.newHashMap(ImmutableMap.<String, Object>builder()
                        .put("url", "https://m.ke.com/{cityId}/ershoufang/pg{pageNo}/?_t=1")
                        .put("cityId", cityId)
                        .put("pageNo", pageNo)
                        .put("pageSize", 30)
                        .put("collection", "beikezhaofang_list")
                        .build()
                )
        );
        task.setStage("init");
    }
}
