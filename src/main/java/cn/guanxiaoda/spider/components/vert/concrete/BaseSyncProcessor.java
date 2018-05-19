package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.monitor.TaskMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseSyncProcessor implements IProcessor<Task> {


    @Override
    public void process(Task task, ICallBack callback) {


        if (doProcess(task)) {

            try {
                callback.call(task);
            } catch (Exception e) {
                log.error("callback failure", e);
            }
        }
    }

    protected abstract boolean doProcess(Task task);
}
