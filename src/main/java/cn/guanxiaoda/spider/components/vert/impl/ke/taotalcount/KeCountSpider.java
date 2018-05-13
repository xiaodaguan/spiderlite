package cn.guanxiaoda.spider.components.vert.impl.ke.taotalcount;

import cn.guanxiaoda.spider.components.vert.BaseSpider;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "keCountSpider")
public class KeCountSpider extends BaseSpider {

    @Autowired @Qualifier("keCountStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("keFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("keCountParser") IProcessor<Task> parser;
    @Autowired @Qualifier("keCountPersister") IProcessor<Task> persister;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);

        Stream.of(
                //城市列表
                "sy",
                "bj"
        ).map(cityId ->
                Task.builder().ctx(
                        Maps.newHashMap(ImmutableMap.<String, Object>builder()
                                .put("cityId", cityId)
                                .build())
                ).name("wx_papers").build()
        ).forEach(this::launch);
    }

}
