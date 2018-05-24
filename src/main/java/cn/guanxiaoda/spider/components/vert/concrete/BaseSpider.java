package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.conf.FastJsonConf;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.monitor.TaskMonitor;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
@Slf4j
@Component
public abstract class BaseSpider {

    @Autowired @Qualifier("mongoClient") protected IMongoDbClient mongoClient;
    @Autowired TaskMonitor monitor;
    /**
     * spider name 全局唯一
     */
    private String name;
    @Autowired private EventBus eb;

    protected void setStarter(BaseProcessor processor) {
        if (Strings.isNullOrEmpty(name)) {
            log.error("please set spider name first!");
            return;
        }
        processor.setEb(eb);
        this.eb.consumer(this.name + ":start", (Handler<Message<Task>>) msg -> {
            Task task = msg.body();
            if (!task.getName().equals(this.name)) {
                log.error("Task::name 与 Spider::name 不一致！");
                return;
            }
            task.setStage("create");
            monitor.tell(msg.body());
            log.info("[AFTER STAGE]{}: {}", processor.getClass().getSimpleName(), JSON.toJSONString(task, FastJsonConf.filter));
            eb.send(this.name + ":" + processor.getClass().getSimpleName(), task);
        });


    }


    /**
     * @param handler
     * @param next    只提供地址
     */
    protected void addProcessor(BaseProcessor handler, BaseProcessor... next) {
        if (Strings.isNullOrEmpty(name)) {
            log.error("please set spider name first!");
            return;
        }
        this.eb.consumer(this.name + ":" + handler.getClass().getSimpleName(), (Handler<Message<Task>>) msg -> {
            Task task = msg.body();
            handler.process(task, (t) -> {
                monitor.tell(msg.body());
                log.info("[AFTER STAGE]{}: {}", handler.getClass().getSimpleName(), JSON.toJSONString(t, FastJsonConf.filter));
                if (next.length > 0) {

                    Arrays.stream(next).forEach(pro -> {
                        pro.setEb(eb);
                        eb.send(this.name + ":" + pro.getClass().getSimpleName(), t);
                    });
                }
            });


        });

    }

    /**
     * @param handler
     */
    protected void setTerminate(BaseProcessor handler) {
        if (Strings.isNullOrEmpty(name)) {
            log.error("please set spider name first!");
            return;
        }
        this.eb.consumer(this.name + ":" + handler.getClass().getSimpleName(), (Handler<Message<Task>>) msg -> {
            handler.process(msg.body(), (t) -> {});
            log.info("[AFTER STAGE]{}: {}", handler.getClass().getSimpleName(), JSON.toJSONString(msg.body(), FastJsonConf.filter));
            monitor.tell(msg.body());
            monitor.recordFinish(msg.body());
        });
    }

    public void launch(Task task) {
        if (Strings.isNullOrEmpty(name)) {
            log.error("please set name for spider");
            return;
        }

        try {
            this.eb.send(this.name + ":start", task);
        } catch (NullPointerException e) {
            log.error("launch action must after starter registered!");
        }
    }

    protected void setName(String name) {
        this.name = name;
    }


}
