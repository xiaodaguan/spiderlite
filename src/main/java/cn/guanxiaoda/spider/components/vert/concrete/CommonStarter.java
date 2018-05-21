package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Component("commonStarter")
public class CommonStarter extends BaseSyncProcessor {

    @Override
    public boolean doProcess(Task task) {
        task.setStage("init");
        return true;
    }
}
