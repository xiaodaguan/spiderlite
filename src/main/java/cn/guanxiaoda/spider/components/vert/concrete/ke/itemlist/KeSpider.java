package cn.guanxiaoda.spider.components.vert.concrete.ke.itemlist;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
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

    @Autowired @Qualifier("keStarter") BaseProcessor starter;
    @Autowired @Qualifier("keFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("keParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;

    public void start() {
        setName("beikezhaofang_list");
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
