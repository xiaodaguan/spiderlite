# spiderlite
轻量级爬虫，基于springboot+vertx


## 已完成的spider

- 微信公众号文章
  - 这个是带登录状态的抓取，抓了一会被封了--!

- 贝壳找房(链家)二手房列表

- 拉勾网职位

## todo
 
### 要抓的




### 系统优化

- ~~微信定时自动获取登录状态~~

- [x]代理

- [x]抓取速度统一配置，~~动态变速~~


- 统一任务启动入口


- [x]某个步骤失败不继续推送

- 任务相关统计

- 数据源：es 

## 食用方式
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


