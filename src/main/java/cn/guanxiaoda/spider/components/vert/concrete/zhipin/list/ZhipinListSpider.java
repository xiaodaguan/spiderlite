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
import java.util.stream.Stream;

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
//        addProcessor(parser, persister, flipper);
        addProcessor(parser, persister);
//        addProcessor(flipper, fetcher);
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
                        // max page
                        1
                ))
                .peek(entry -> log.info("city={}, page={}", entry.getKey(), entry.getValue()))
                .flatMap(pair ->
                        IntStream.range(1, pair.getValue() + 1).mapToObj(
                                pageNo ->

                                        Stream.of(
                                                /*bat*/
                                                "百度",
                                                "阿里巴巴",
                                                "腾讯",
                                                "爱奇艺",
                                                "支付宝",
                                                "蚂蚁金服",
                                                /*tmdj*/
                                                "京东",
                                                "今日头条",
                                                "美团",
                                                "滴滴",
                                                "网易",
                                                "华为",
                                                "小米",
                                                "360",
                                                "搜狗",
                                                "微博",
                                                "新浪",
                                                "58同城",
                                                "携程",
                                                "去哪儿",
                                                "乐视",
                                                "饿了么",
                                                "京东金融",
                                                "优酷",
                                                "知乎",
                                                "链家",
                                                "快手",
                                                "陌陌",
                                                /*外企*/
                                                "微软",
                                                "谷歌",
                                                "Amazon",
                                                "亚马逊"
                                        ).map(kw -> Task.builder()
                                                .name("zhipin_list")
                                                .ctx(
                                                        Maps.newHashMap(
                                                                ImmutableMap.<String, Object>builder()
                                                                        .put("city", pair.getKey())
                                                                        .put("keyword", kw)
                                                                        .put("pageNo", pageNo)
                                                                        .put("collection", "zhipin_list")
                                                                        .build()
                                                        )
                                                ).build())


                        ).flatMap(stream -> stream)

                )
                .forEach(this::launch);
    }

}
