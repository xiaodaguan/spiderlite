package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("wxStarter")
public class Starter implements IProcessor<Task> {
    @Override
    public void process(Task task) {
            String fakeId = Optional.of(task.getCtx()).map(ctx -> ctx.get("fakeId")).map(String::valueOf).orElse("");
            String token = Optional.of(task.getCtx()).map(ctx -> ctx.get("token")).map(String::valueOf).orElse("");
            String cookies = Optional.of(task.getCtx()).map(ctx -> ctx.get("cookies")).map(String::valueOf).orElse("");
            task.getCtx().putAll(
                    Maps.newHashMap(ImmutableMap.<String, Object>builder()
                                    .put("url", "https://mp.weixin.qq.com/cgi-bin/appmsg?token={token}&lang=zh_CN&f=json&ajax=1&random=" +
                                            Math.random() + "&action=list_ex&begin={begin}&count=10&query=&fakeid={fakeId}&type=9")
//                            .put("fakeId", "MzAxOTc0NzExNg%3D%3D")
                                    .put("fakeId", fakeId)
                                    .put("token", token)
                                    .put("cookies", cookies)
                                    .put("pageNo", 1)
                                    .put("pageSize", 5)
                                    .put("maxPage", 20)
                                    .put("collection", "wx_public_papers")
                                    .build()
                    )
            );
            task.setStage("init");
    }
}
