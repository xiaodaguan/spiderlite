package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail;

import cn.guanxiaoda.spider.components.vert.BaseSpider;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "lagouDetailSpider")
public class LagouDetailSpider extends BaseSpider {

    @Autowired @Qualifier("lagouDetailStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("lagouDetailFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("lagouDetailParser") IProcessor<Task> parser;
    @Autowired @Qualifier("itemPersister") IProcessor<Task> persister;

    public void start() {

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
                    return Task.builder()
                            .name("lagou-detail")
                            .ctx(Maps.newHashMap(
                                    ImmutableMap.<String, Object>builder()
                                            .put("collection","lagou_detail")
                                            .put("positionId", doc.getString("positionId"))
                                            .put("uniqueKey", doc.get("uniqueKey"))
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
                })
                .forEach(this::launch);
    }

}
