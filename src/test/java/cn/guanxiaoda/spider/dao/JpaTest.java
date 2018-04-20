package cn.guanxiaoda.spider.dao;

import cn.guanxiaoda.spider.Application;
import cn.guanxiaoda.spider.dao.TicketDealInfoNewRepository;
import cn.guanxiaoda.spider.dao.TicketDealInfoOldRepository;
import cn.guanxiaoda.spider.models.TicketDealInfoNew;
import cn.guanxiaoda.spider.models.TicketDealInfoOld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/4/19
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class JpaTest {

    @Autowired
    TicketDealInfoOldRepository oldRepository;
    @Autowired
    TicketDealInfoNewRepository newRepository;

    @Test
    public void test() {
        List<TicketDealInfoOld> infoListOld = oldRepository.findInfosOld();
        List<TicketDealInfoNew> infoListNew = newRepository.findInfosNew();

        System.out.println(infoListOld.size());
        System.out.println(infoListNew.size());

        Map<String, List<TicketDealInfoNew>> newMap = infoListNew.stream().map(o -> {
            TicketDealInfoNew info = new TicketDealInfoNew();
            info.setPoiId(o.getPoiId());
            info.setDealId(o.getDealId());
            info.setDealName(o.getDealName());
            info.setInclude(o.getInclude());
            return info;
        }).collect(Collectors.groupingBy(n -> n.getPoiId() + "_" + n.getDealId()));

        AtomicInteger null2Val = new AtomicInteger();
        AtomicInteger val2Null = new AtomicInteger();
        AtomicInteger bothNull = new AtomicInteger();
        AtomicInteger bothVal = new AtomicInteger();
        AtomicInteger valEquals = new AtomicInteger();
        AtomicInteger valDiff = new AtomicInteger();

        System.out.println("starting...");
        infoListOld.forEach(o -> {
            String key = o.getPoiId() + "_" + o.getDealId();
            TicketDealInfoNew n = newMap.get(key).get(0);
            if (o.getInclude() == null && n.getInclude() != null) {
                null2Val.incrementAndGet();
            } else if (o.getInclude() != null && n.getInclude() == null) {
                val2Null.incrementAndGet();
            } else if (o.getInclude() == null && n.getInclude() == null) {
                bothNull.incrementAndGet();
            } else {
                bothVal.incrementAndGet();
                if (o.getInclude().equalsIgnoreCase(n.getInclude())) {
                    valEquals.incrementAndGet();
                } else {
                    valDiff.incrementAndGet();
                }
            }
        });
        System.out.println("old->new");
        System.out.println("null2Val:" + null2Val);
        System.out.println("val2Null:" + val2Null);
        System.out.println("bothNull:" + bothNull);
        System.out.println("bothVal:" + bothVal);
        System.out.println("valEquals:" + valEquals);
        System.out.println("valDiff:" + valDiff);
    }
}
