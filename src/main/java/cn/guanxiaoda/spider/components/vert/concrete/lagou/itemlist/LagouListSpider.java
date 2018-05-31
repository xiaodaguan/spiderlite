package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "lagouListSpider")
@Slf4j
public class LagouListSpider extends BaseSpider {

    @Autowired @Qualifier("lagouListStarter") BaseProcessor starter;
    @Autowired @Qualifier("lagouListFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("lagouListParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {
        setName("lagou_list");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);

        List<Document> docs = mongoClient.findAllDocs("lagou_count");
        log.info("get {} docs: {}", docs.size(), JSON.toJSONString(docs));
        docs.stream()
                .map(doc -> Maps.immutableEntry(
                        doc.getString("name"),
                        (int) Math.ceil((1.0 * doc.getInteger("count")) / 30))
                )
                .peek(entry -> log.info("city={}, page={}", entry.getKey(), entry.getValue()))
                .flatMap(pair ->
                        IntStream.range(1, pair.getValue() + 1).mapToObj(
                                pageNo ->
                                        Task.builder()
                                                .name("lagou_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("city", pair.getKey())
                                                                        .put("positionName", "后端")
                                                                        .put("pageNo", pageNo)
                                                                        .put("collection", "lagou_list")
                                                                        .build()
                                                        )
                                                ).build()

                        )

                )
                .forEach(this::launch);
    }

}
