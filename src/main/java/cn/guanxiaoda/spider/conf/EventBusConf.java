package cn.guanxiaoda.spider.conf;

import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.models.TaskCodec;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Configuration
public class EventBusConf {
    private static VertxOptions opt = new VertxOptions();

    static {
        opt.setMaxEventLoopExecuteTime((7 * 24 * 3600L) * 1000 * 1000 * 1000);
    }

    @Bean
    public EventBus eventBus() {
        Vertx vertx = Vertx.vertx(opt);
        EventBus eb = vertx.eventBus();
        eb.registerDefaultCodec(Task.class, new TaskCodec());
        return eb;
    }
}
