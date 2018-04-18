package cn.guanxiaoda.spider.components.vert;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
public abstract class BaseSpider {
    @Autowired
    private EventBus eb;
    private String starterAddr;

    /**
     * @param handler
     * @param next    只提供地址
     */
    protected void addProcessor(IProcessor handler, IProcessor next) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> eb.send(next.getClass().getName(), handler.process(msg.body())));
    }

    /**
     * @param handler
     */
    protected void setTerminate(IProcessor handler) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> handler.process(msg.body()));
    }

    protected void send(Task task) {
        this.eb.send(starterAddr, task);
    }

    protected void setStarter(IProcessor<Task> processor) {
        this.starterAddr = processor.getClass().getName();
    }
}
