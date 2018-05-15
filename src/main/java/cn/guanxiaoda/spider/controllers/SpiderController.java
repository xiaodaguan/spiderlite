package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.components.vert.concrete.ke.itemlist.KeSpider;
import cn.guanxiaoda.spider.components.vert.concrete.ke.taotalcount.KeCountSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail.LagouDetailSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.itemlist.LagouListSpider;
import cn.guanxiaoda.spider.components.vert.concrete.lagou.totalcount.LagouCountSpider;
import cn.guanxiaoda.spider.components.vert.concrete.wx.WxSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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

    @RequestMapping("/wx/start")
    public void wxStart() { wxSpider.start(); }

    @RequestMapping("/ke/list/start")
    public void keStart() { keSpider.start(); }

    @RequestMapping("/ke/count/start")
    public void keCountStart() { keCountSpider.start(); }

    @RequestMapping("lagou/count/start")
    public void lagouCountStart() { lagouCountSpider.start();}


    @RequestMapping("lagou/list/start")
    public void lagouListStart() { lagouListSpider.start();}

    @RequestMapping("lagou/detail/start")
    public void lagouDetailStart() { lagouDetailSpider.start();}
}
