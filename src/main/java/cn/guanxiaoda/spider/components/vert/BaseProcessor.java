package cn.guanxiaoda.spider.components.vert;

import cn.guanxiaoda.spider.models.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseProcessor implements IProcessor<Task> {
    @Override
    public void process(Task task, ICallBack callback) {
        doProcess(task);
        try {
            callback.call(task);
        } catch (Exception e) {
            log.error("callback failure", e);
        }
    }
}
