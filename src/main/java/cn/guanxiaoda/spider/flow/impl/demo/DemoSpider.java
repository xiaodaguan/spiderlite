package cn.guanxiaoda.spider.flow.impl.demo;

import cn.guanxiaoda.spider.flow.IFetcher;
import cn.guanxiaoda.spider.flow.IParser;
import cn.guanxiaoda.spider.models.Task;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component
public class DemoSpider {

    @Autowired
    IFetcher<Task> fetcher;
    @Autowired
    IParser<Task> parser;
    @Autowired
    private EventBus eb;


    public void start() {

        eb.consumer(fetcher.getClass().getName(), (Handler<Message<Task>>) msg -> eb.send(parser.getClass().getName(), fetcher.fetch(msg.body())));
        eb.consumer(parser.getClass().getName(), (Handler<Message<Task>>) msg -> eb.send(DemoSpider.class.getName(), parser.parse(msg.body())));
        eb.consumer(DemoSpider.class.getName(), (Handler<Message<Task>>) msg -> System.out.println(msg.body()));

        eb.send(fetcher.getClass().getName(), new Task().setName("test-task").setUrl("http://demo.spider.cn"));


    }
}
