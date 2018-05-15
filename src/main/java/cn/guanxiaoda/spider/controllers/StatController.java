package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.monitor.TaskMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
@Controller
@RequestMapping("/monitor")
public class StatController {
    @Autowired @Qualifier("taskMonitor") TaskMonitor monitor;

    @RequestMapping("/total")
    public String getTotalStat() {
        return monitor.getTotalStat();
    }

    @RequestMapping("/detail")
    public String getDetailStat() {
        return monitor.getDetailStat();
    }

}
