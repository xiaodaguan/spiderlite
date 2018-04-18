package cn.guanxiaoda.spider.components.vert;

import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.models.TaskCodec;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Configuration
public class EventBusConf {

    @Bean
    public EventBus eventBus() {
        Vertx vertx = Vertx.vertx();
        EventBus eb = vertx.eventBus();
        eb.registerDefaultCodec(Task.class, new TaskCodec());

        return eb;
    }
}
