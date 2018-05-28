package cn.guanxiaoda.spider.components.vert.concrete.zhipin.list;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "zhipinListSpider")
@Slf4j
public class ZhipinListSpider extends BaseSpider {

    @Autowired @Qualifier("commonStarter") BaseProcessor starter;
    @Autowired @Qualifier("zhipinListFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("zhipinListParser") BaseProcessor parser;
    @Autowired @Qualifier("commonFlipper") BaseProcessor flipper;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setName("zhipin_list");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister, flipper);
        addProcessor(flipper, fetcher);
        setTerminate(persister);


        List<String> cities = Arrays.asList(
                "101010100",
                "101020100",
                "101280100",
                "101280600",
                "101210100"
        );
        cities.stream()
                .filter("101010100"::equals)
                .map(city -> Maps.immutableEntry(
                        city,
                        1
                ))
                .peek(entry -> log.info("city={}, page={}", entry.getKey(), entry.getValue()))
                .flatMap(pair ->
                        IntStream.range(1, pair.getValue() + 1).mapToObj(
                                pageNo ->
                                        Task.builder()
                                                .name("zhipin_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("city", pair.getKey())
                                                                        .put("keyword", "java")
                                                                        .put("pageNo", pageNo)
                                                                        .put("collection", "zhipin_list")
                                                                        .build()
                                                        )
                                                ).build()

                        )

                )
                .forEach(this::launch);
    }

}
