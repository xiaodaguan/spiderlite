package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.conf.FastJsonConf;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import io.vertx.core.eventbus.EventBus;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guanxiaoda
 * @date 2018/5/23
 */
@Slf4j
public abstract class BaseProcessor implements IProcessor<Task> {

    EventBus eb;

    public void setEb(EventBus eb) {
        this.eb = eb;
    }

    @Override
    public void process(Task task, ICallBack callback) {

    }

    @Synchronized
    protected void retry(Task task) {
        if (task.getRetryNo() > (task.getMaxRetry() == 0 ? 5 : task.getMaxRetry())) {
            log.error("[MAX RETRY]drop task:{}", JSON.toJSONString(task, FastJsonConf.filter));
            return;
        }
        task.setRetryNo(task.getRetryNo() + 1);
        task.setStage("retry");
        log.info("[RETRY]retry task:{}", JSON.toJSONString(task, FastJsonConf.filter));
        this.eb.send(task.getName() + ":start", task);
    }

}
