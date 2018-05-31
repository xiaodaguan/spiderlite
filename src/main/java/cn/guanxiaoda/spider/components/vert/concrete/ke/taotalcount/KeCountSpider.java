package cn.guanxiaoda.spider.components.vert.concrete.ke.taotalcount;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
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

    @Autowired @Qualifier("keCountStarter") BaseProcessor starter;
    @Autowired @Qualifier("keFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("keCountParser") BaseProcessor parser;
    @Autowired @Qualifier("countPersister") BaseProcessor persister;

    public void start() {
        setName("beikezhaofang_count");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);

        Stream.of(
                //城市列表
                "bj",
                "sh",
                "gz",
                "sz",
                "cd",
                "hz"

        ).map(cityId ->
                Task.builder()
                        .ctx(
                                Maps.newHashMap(ImmutableMap.<String, Object>builder()
                                        .put("cityId", cityId)
                                        .build())
                        ).name("beikezhaofang_count").build()
        ).forEach(this::launch);
    }

}
