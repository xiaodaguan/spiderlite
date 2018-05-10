package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("wxStarter")
public class Starter implements IProcessor<Task> {
    @Override
    public Task process(Task task) {
        task.setCtx(Maps.newHashMap(
                ImmutableMap.<String, Object>builder().put("url", "https://mp.weixin.qq.com/cgi-bin/appmsg?token=861722949&lang=zh_CN&f=json&ajax=1&random=0.6801171191754858&action=list_ex&begin={begin}&count=5&query=&fakeid=MzAxMTA0ODE3Mw%3D%3D&type=9")
                        .put("pageNo", 1)
                        .build()
                )
        );
        task.setStage("init");
        return task;
    }
}
