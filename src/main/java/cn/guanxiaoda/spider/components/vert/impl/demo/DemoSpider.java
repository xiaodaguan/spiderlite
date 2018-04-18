package cn.guanxiaoda.spider.components.vert.impl.demo;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.components.vert.BaseSpider;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component
public class DemoSpider extends BaseSpider {

    @Autowired
    @Qualifier("demoFetcher")
    IProcessor<Task> fetcher;
    @Autowired
    @Qualifier("demoParser")
    IProcessor<Task> parser;
    @Autowired
    @Qualifier("demoPrinter")
    IProcessor<Task> printer;

    public void start() {

        setStarter(fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, printer);
        setTerminate(printer);

        IntStream.range(0, 10)
                .mapToObj(i -> new Task().setName("demo-task-" + i).setUrl("http://demo.url.cn/pn/" + i))
                .parallel()
                .forEach(this::send);
    }

}
