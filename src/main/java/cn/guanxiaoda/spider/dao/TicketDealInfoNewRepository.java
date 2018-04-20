package cn.guanxiaoda.spider.dao;

import cn.guanxiaoda.spider.models.TicketDealInfoNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author guanxiaoda
 * @date 2018/4/19
 */

public interface TicketDealInfoNewRepository extends JpaRepository<TicketDealInfoNew, Long> {


    @Query("select t.poiId, t.dealId, t.dealName, t.include from TicketDealInfoNew t where t.lastUpdateTime >= '2018-04-19 13:00:00'")
    List<TicketDealInfoNew> findInfosNew();

}
