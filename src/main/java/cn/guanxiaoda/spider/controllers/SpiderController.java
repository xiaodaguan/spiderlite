package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.components.vert.impl.ke.itemlist.KeSpider;
import cn.guanxiaoda.spider.components.vert.impl.ke.taotalcount.KeCountSpider;
import cn.guanxiaoda.spider.components.vert.impl.wx.WxSpider;
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

    @RequestMapping("/wx/start")
    public void wxStart() {
        wxSpider.start();
    }

    @RequestMapping("/ke/list/start")
    public void keStart() {
        keSpider.start();
    }

    @RequestMapping("/ke/count/start")
    public void keCountStart() {
        keCountSpider.start();
    }
}
