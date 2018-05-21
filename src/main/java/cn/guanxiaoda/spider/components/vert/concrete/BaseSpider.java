package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.monitor.TaskMonitor;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
@Slf4j
public abstract class BaseSpider {


    @Autowired @Qualifier("mongoClient") protected IMongoDbClient mongoClient;
    @Autowired TaskMonitor monitor;
    @Autowired
    private EventBus eb;
    private String starterAddr;

    /**
     * @param handler
     * @param next    只提供地址
     */
    protected void addProcessor(IProcessor handler, IProcessor... next) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> {
            Task task = msg.body();
            handler.process(task, (t) -> {
                monitor.tell(msg.body());
                log.info("[AFTER STAGE]{}: {}", handler.getClass().getSimpleName(), JSON.toJSONString(t));
                if (next.length > 0) {
                    Arrays.stream(next).forEach(pro -> eb.send(pro.getClass().getName(), t));
                }
            });


        });

    }

    /**
     * @param handler
     */
    protected void setTerminate(IProcessor handler) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> {
            handler.process(msg.body(), (t) -> {});
            log.info("[AFTER STAGE]{}: {}", handler.getClass().getSimpleName(), JSON.toJSONString(msg.body()));
            monitor.tell(msg.body());
            monitor.recordFinish(msg.body());
        });
    }

    protected void launch(Task task) {
        try {
            this.eb.send(starterAddr, task);
        } catch (NullPointerException e) {
            log.error("launch action must after starter registered!");
        }
    }

    protected void setStarter(IProcessor processor) {
        this.starterAddr = processor.getClass().getName();
    }
}
