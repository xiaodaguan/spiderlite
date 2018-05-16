package cn.guanxiaoda.spider.components.vert.concrete.ke.itemlist;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
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
@Component(value = "keSpider")
public class KeSpider extends BaseSpider {

    @Autowired @Qualifier("keStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("keFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("keParser") IProcessor<Task> parser;
    @Autowired @Qualifier("itemPersister") IProcessor<Task> persister;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        mongoClient.findAllDocs("beikezhaofang_count")
                .parallelStream()
                .map(doc -> Maps.immutableEntry(
                        doc.getString("name"),
                        (int) Math.ceil((1.0 * doc.getInteger("count")) / 30)
                        )
                )
                .flatMap(pair ->
                        IntStream.range(1, (pair.getValue() + 1) > 100 ? 101 : pair.getValue() + 1).mapToObj(
                                pageNo ->
                                        Task.builder()
                                                .name("beike_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("cityId", pair.getKey())
                                                                        .put("pageNo", pageNo)
                                                                        .build()
                                                        )
                                                ).build()

                        )

                )
                .forEach(this::launch);


    }

}
