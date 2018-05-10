package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.components.vert.BaseSpider;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "wxSpider")
public class App extends BaseSpider {

    @Autowired @Qualifier("wxStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("wxFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("wxParser") IProcessor<Task> parser;
    @Autowired @Qualifier("wxPrinter") IProcessor<Task> printer;
    @Autowired @Qualifier("wxPager") IProcessor<Task> pager;

    public void start() {
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, printer);
        setTerminate(printer);

        /*翻页*/
        addProcessor(parser, pager);
        addProcessor(pager, fetcher);

        send(Task.builder().name("微信公众号-有车以后-文章").build());
    }

}
