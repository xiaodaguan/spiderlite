package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist;

import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "lagouListSpider")
public class LagouListSpider extends BaseSpider {

    @Autowired @Qualifier("lagouListStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("lagouListFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("lagouListParser") IProcessor<Task> parser;
    @Autowired @Qualifier("itemPersister") IProcessor<Task> persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);

        mongoClient.findAllDocs("lagou_count")
                .parallelStream()
                .map(doc -> Maps.immutableEntry(
                        doc.getString("name"),
                        (int) Math.ceil((1.0 * doc.getInteger("count")) / 30)
                        )
                )
                .flatMap(pair ->
                        IntStream.range(1, pair.getValue() + 1).mapToObj(
                                pageNo ->
                                        Task.builder()
                                                .name("lagou_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("city", pair.getKey())
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
