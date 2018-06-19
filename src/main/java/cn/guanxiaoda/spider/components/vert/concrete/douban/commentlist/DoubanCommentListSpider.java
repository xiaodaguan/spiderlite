package cn.guanxiaoda.spider.components.vert.concrete.douban.commentlist;

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

import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "doubanCommentListSpider")
@Slf4j
public class DoubanCommentListSpider extends BaseSpider {

    @Autowired @Qualifier("commonStarter") BaseProcessor starter;
    @Autowired @Qualifier("doubanCommentListFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("doubanCommentListParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setName("doubanComment_list");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        mongoClient.findAllDocs("doubanMovie_list")
                .stream()
                .map(doc -> doc.getString("movieId"))
                .map(movieId -> Maps.immutableEntry(movieId, 10))
//        Stream.of(Maps.immutableEntry("27133303", 10))
                .flatMap(pair -> IntStream.range(1, pair.getValue() + 1)
                        .mapToObj(pageNo -> ImmutableMap.<String, Integer>builder()
                                .put("movieId", Integer.valueOf(pair.getKey()))
                                .put("pageNo", pageNo)
                                .build()))
                .map(map ->
                        Task.builder()
                                .name("doubanComment_list")
                                .ctx(
                                        Maps.newHashMap(
                                                ImmutableMap.<String, Object>builder()
                                                        .put("movieId", map.get("movieId"))
                                                        .put("pageNo", map.get("pageNo"))
                                                        .put("collection", "doubanComment_list")
                                                        .build()
                                        )
                                ).build())
                .sorted((o1, o2) -> (int) o2.getCtx().get("pageNo") - (int) o1.getCtx().get("pageNo"))
                .forEachOrdered(this::launch);
    }

}
