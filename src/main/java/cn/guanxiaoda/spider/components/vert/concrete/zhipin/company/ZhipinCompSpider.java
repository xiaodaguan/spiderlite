package cn.guanxiaoda.spider.components.vert.concrete.zhipin.company;

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

import java.util.Objects;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "zhipinCompSpider")
@Slf4j
public class ZhipinCompSpider extends BaseSpider {

    @Autowired @Qualifier("commonStarter") BaseProcessor starter;
    @Autowired @Qualifier("zhipinCompFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("zhipinCompParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoClient;

    public void start() {

        setName("zhipin_comp");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);


        mongoClient.findAllDocs("zhipin_detail")
                .stream()
                .map(doc -> {
                    try {
                        return Task.builder()
                                .name("zhipin_comp")
                                .ctx(Maps.newHashMap(
                                        ImmutableMap.<String, Object>builder()
                                                .put("collection", "zhipin_comp")
                                                .put("compHref", doc.getString("compHref"))
                                                .put("coordinate", doc.get("coordinate"))
                                                .put("city", doc.getString("city"))
                                                .put("companyName", doc.getString("companyName"))
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
