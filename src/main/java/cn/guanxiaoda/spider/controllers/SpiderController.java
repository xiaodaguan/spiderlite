package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.components.vert.concrete.ke.itemlist.KeSpider;
import cn.guanxiaoda.spider.components.vert.concrete.ke.taotalcount.KeCountSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail.LagouDetailSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist.LagouListSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.totalcount.LagouCountSpider;
import cn.guanxiaoda.spider.components.vert.concrete.wx.WxSpider;
import cn.guanxiaoda.spider.components.vert.concrete.zhipin.detail.ZhipinDetailSpider;
import cn.guanxiaoda.spider.components.vert.concrete.zhipin.list.ZhipinListSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Controller
@RequestMapping("/spider")
public class SpiderController {

    @Autowired @Qualifier("wxSpider") WxSpider wxSpider;

    @Autowired @Qualifier("keSpider") KeSpider keSpider;
    @Autowired @Qualifier("keCountSpider") KeCountSpider keCountSpider;

    @Autowired @Qualifier("lagouCountSpider") LagouCountSpider lagouCountSpider;
    @Autowired @Qualifier("lagouListSpider") LagouListSpider lagouListSpider;
    @Autowired @Qualifier("lagouDetailSpider") LagouDetailSpider lagouDetailSpider;

    @Autowired @Qualifier("zhipinListSpider") ZhipinListSpider zhipinListSpider;
    @Autowired @Qualifier("zhipinDetailSpider") ZhipinDetailSpider zhipinDetailSpider;

    @GetMapping("/wx/start")
    public void wxStart() { wxSpider.start(); }

    @GetMapping("/ke/list/start")
    public void keStart() { keSpider.start(); }

    @GetMapping("/ke/count/start")
    public void keCountStart() { keCountSpider.start(); }

    @GetMapping("lagou/count/start")
    public void lagouCountStart() { lagouCountSpider.start();}


    @GetMapping("lagou/list/start")
    public void lagouListStart() { lagouListSpider.start();}

    @GetMapping("lagou/detail/start")
    public void lagouDetailStart() { lagouDetailSpider.start();}


    @GetMapping("zhipin/list/start")
    public void zhipinListStart() { zhipinListSpider.start();}


    @GetMapping("zhipin/detail/start")
    public void zhipinDetailStart() { zhipinDetailSpider.start();}

}
