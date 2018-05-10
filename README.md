# spiderlite
轻量级爬虫，基于springboot+vertx

``` java
@Component(value = "wxSpider")
public class WxSpider extends BaseSpider {

    @Autowired @Qualifier("wxStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("wxFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("wxParser") IProcessor<Task> parser;
    @Autowired @Qualifier("wxPrinter") IProcessor<Task> printer;
    @Autowired @Qualifier("wxPager") IProcessor<Task> pager;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, printer, pager);
        addProcessor(pager, fetcher);
        setTerminate(printer);

        send(Task.builder().name("微信公众号-有车以后-文章").build());
    }

}

```
