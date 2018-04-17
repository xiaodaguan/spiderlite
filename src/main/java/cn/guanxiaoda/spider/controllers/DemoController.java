package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.flow.impl.demo.DemoSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    DemoSpider spider;

    @RequestMapping("/start")
    public void start() {
        spider.start();
    }
}
