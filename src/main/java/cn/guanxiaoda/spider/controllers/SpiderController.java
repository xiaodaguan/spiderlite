package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.components.vert.impl.wx.App;
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

    @Autowired @Qualifier("wxSpider") App spider;

    @RequestMapping("/wx/start")
    public void start() {
        spider.start();
    }
}
