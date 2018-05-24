package cn.guanxiaoda.spider.components.vert.concrete.zhipin.detail;

import cn.guanxiaoda.spider.annotation.Processor;
import cn.guanxiaoda.spider.components.vert.IProcessor;
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
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "zhipinDetailSpider")
@Slf4j
public class ZhipinDetailSpider extends BaseSpider {

    @Autowired @Qualifier("commonStarter") BaseProcessor starter;
    @Autowired @Qualifier("zhipinDetailFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("zhipinDetailParser") BaseProcessor parser;
    @Autowired @Qualifier("commonFlipper") BaseProcessor flipper;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setName("zhipinDetailSpider");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        mongoClient.findAllDocs("zhipin_list")
                .stream()
                .map(doc -> {
                    try {
                        return Task.builder()
                                .name("zhipin-detail")
                                .ctx(Maps.newHashMap(
                                        ImmutableMap.<String, Object>builder()
                                                .put("collection", "zhipin_detail")
                                                .put("href", doc.getString("href"))
                                                .put("title", doc.get("title"))
                                                .put("salary", doc.getString("salary"))
                                                .put("companyName", doc.getString("companyName"))
                                                .put("crawlTime", doc.getString("crawlTime"))
                                                .put("city", doc.getString("city"))
                                                .put("uniqueKey", doc.getString("uniqueKey"))
                                                .build()
                                ))
                                .build();
                    } catch (Exception e) {
                        log.error("generate task failure", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(this::launch);

    }

}
