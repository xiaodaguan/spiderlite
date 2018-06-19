package cn.guanxiaoda.spider.components.vert.concrete.douban.movielist;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "doubanMovieListSpider")
@Slf4j
public class DoubanMovieListSpider extends BaseSpider {

    @Autowired @Qualifier("commonStarter") BaseProcessor starter;
    @Autowired @Qualifier("doubanMovieListFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("doubanMovieListParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setName("doubanMovie_list");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        Lists.newArrayList(
                "喜剧", "动作", "爱情", "科幻", "悬疑", "惊悚", "恐怖", "犯罪", "同性", "音乐", "歌舞",
                "传记", "历史", "战争", "西部", "奇幻", "冒险", "灾难", "武侠", "情色")
                .stream()
                .flatMap(tag ->
                        IntStream.range(0, 101)

                                .mapToObj(pageNo ->
                                        Task.builder()
                                                .name("doubanMovie_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("tag", tag)
                                                                        .put("pageNo", pageNo)
                                                                        .put("collection", "doubanMovie_list")
                                                                        .build()
                                                        )
                                                ).build())

                                .sorted(Comparator.comparingInt(o -> (int) o.getCtx().get("pageNo")))
                )

                .forEachOrdered(this::launch);
    }

}
