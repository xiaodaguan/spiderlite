package cn.guanxiaoda.spider.components.vert;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
@Slf4j
public abstract class BaseSpider {
    @Autowired
    private EventBus eb;
    private String starterAddr;

    /**
     * @param handler
     * @param next    只提供地址
     */
    protected void addProcessor(IProcessor handler, IProcessor next) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> {
            boolean stopFlag = Optional.ofNullable(msg.body().getCtx())
                    .map(ctx -> ctx.get("stopSend"))
                    .map(Boolean.class::cast)
                    .orElse(false);
            if (stopFlag) {
                log.info("stop event sending, task={}", JSON.toJSONString(msg.body()));
                return;
            }
            eb.send(next.getClass().getName(), handler.process(msg.body()));
        });
    }

    /**
     * @param handler
     */
    protected void setTerminate(IProcessor handler) {
        this.eb.consumer(handler.getClass().getName(), (Handler<Message<Task>>) msg -> handler.process(msg.body()));
    }

    protected void send(Task task) {
        try {
            this.eb.send(starterAddr, task);
        } catch (NullPointerException e) {
            log.error("send action must after starter registered!");
        }
    }

    protected void setStarter(IProcessor<Task> processor) {
        this.starterAddr = processor.getClass().getName();
    }
}
