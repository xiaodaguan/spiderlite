package cn.guanxiaoda.spider.components.vert.concrete.lagou.totalcount;

import cn.guanxiaoda.spider.components.vert.BaseProcessor;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("lagouCountStarter")
public class Starter extends BaseProcessor {
    @Override
    public void doProcess(Task task) {
        task.setStage("init");
    }
}
