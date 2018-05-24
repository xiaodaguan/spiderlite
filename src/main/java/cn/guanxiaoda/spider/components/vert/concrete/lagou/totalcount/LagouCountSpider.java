package cn.guanxiaoda.spider.components.vert.concrete.lagou.totalcount;

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
@Component(value = "lagouCountSpider")
public class LagouCountSpider extends BaseSpider {

    @Autowired @Qualifier("lagouCountStarter") BaseProcessor starter;
    @Autowired @Qualifier("lagouListFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("lagouCountParser") BaseProcessor parser;
    @Autowired @Qualifier("countPersister") BaseProcessor persister;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        Stream.of(
                //城市列表
                "北京",
                "上海",
                "广州",
                "深圳",
                "杭州"
        ).map(city ->
                Task.builder()
                        .ctx(
                                Maps.newHashMap(ImmutableMap.<String, Object>builder()
                                        .put("city", city)
                                        .put("positionName", "后端")
                                        .put("collection", "lagou_count")
                                        .build())
                        ).name("lagou_count").build()
        ).forEach(this::launch);
    }

}
