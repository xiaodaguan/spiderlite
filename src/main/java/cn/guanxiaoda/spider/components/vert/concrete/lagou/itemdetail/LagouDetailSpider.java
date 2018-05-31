package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail;

import cn.guanxiaoda.spider.components.vert.concrete.BaseProcessor;
import cn.guanxiaoda.spider.components.vert.concrete.BaseSpider;
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
@Component(value = "lagouDetailSpider")
@Slf4j
public class LagouDetailSpider extends BaseSpider {

    @Autowired @Qualifier("lagouDetailStarter") BaseProcessor starter;
    @Autowired @Qualifier("lagouDetailFetcher") BaseProcessor fetcher;
    @Autowired @Qualifier("lagouDetailParser") BaseProcessor parser;
    @Autowired @Qualifier("itemPersister") BaseProcessor persister;

    public void start() {
        setName("lagou_detail");
        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister);
        setTerminate(persister);

        mongoClient.findAllDocs("lagou_list")
                .stream()
                .map(doc -> {
//{
//    "_id" : "daf5e4e92af184fd426b9bcfb8b9450565553117c989ae82b722acaf5c5718d1",
//    "positionId" : "4304259",
//    "uniqueKey" : "4304259",
//    "positionName" : "产品经理",
//    "city" : "北京",
//    "createTime" : "今天 11:38",
//    "salary" : "10k-18k",
//    "companyId" : "40215",
//    "companyLogo" : "image2/M00/07/FB/CgpzWlYBBUmADh0eAADirhIpoYY981.jpg",
//    "companyName" : "兔玩网",
//    "companyFullName" : "北京兔玩在线科技有限公司",
//    "crawlTime" : "2018-05-15 11:51:19"
//}
                    try {
                        return Task.builder()
                                .name("lagou-detail")
                                .ctx(Maps.newHashMap(
                                        ImmutableMap.<String, Object>builder()
                                                .put("collection", "lagou_detail")
                                                .put("positionId", doc.getString("positionId"))
                                                .put("uniqueKey", doc.get("positionId"))
                                                .put("positionName", doc.getString("positionName"))
                                                .put("city", doc.getString("city"))
                                                .put("salary", doc.getString("salary"))
                                                .put("companyId", doc.getString("companyId"))
                                                .put("companyLogo", doc.getString("companyLogo"))
                                                .put("companyName", doc.getString("companyName"))
                                                .put("companyFullName", doc.getString("companyFullName"))
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
