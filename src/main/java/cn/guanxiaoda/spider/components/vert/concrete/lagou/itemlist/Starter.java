package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("lagouListStarter")
public class Starter implements IProcessor<Task> {
    @Override
    public void process(Task task) {
        task.setStage("init");
    }
}
