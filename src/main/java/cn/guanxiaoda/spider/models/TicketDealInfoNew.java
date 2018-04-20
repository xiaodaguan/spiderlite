package cn.guanxiaoda.spider.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: gaoguanguo
 * @date 2017/9/8
 * @modified by
 **/
@Data
@NoArgsConstructor
@Entity
@Table(name = "ticket_deal_info_qunar_online_20180419_14")
public class TicketDealInfoNew implements Serializable {

    /**
     * 唯一id
     */
    @Id
    private long id;

    /**
     * poiid
     */
    private String poiId;

    /**
     * 竞对dealId
     **/
    private String dealId;

    /**
     * dealName
     **/
    private String dealName;

    /**
     * include 费用
     **/
    private String include;

    /**
     * 优惠信息
     **/
    private String couponInfo;

    /**
     * 用途
     */
    private String usage;

    /**
     * 预定shij
     **/
    private String bookTime;

    /**
     * 预定说明
     **/
    private String bookIntro;

    /**
     * 发票说明
     **/
    private String invoice;

    /**
     * 退票规则
     **/
    private String refundRule;

    /**
     * 备用字段
     */
    private String extend;

    /**
     * siteid
     **/
    private int siteId;

    /**
     * source
     **/
    private int source;

    /**
     * 插入时间
     **/
    private LocalDateTime firstAddTime;

    /**
     * 最后更新时间
     **/
    private LocalDateTime lastUpdateTime;
}
