package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.conf.FastJsonConf;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseAsyncProcessor extends BaseProcessor {

    @Override
    public void process(Task task, ICallBack callback) {

        try {
            doProcess(task, callback);
        } catch (Exception e) {
            log.error("async processor process failure task={}", JSON.toJSONString(task, FastJsonConf.filter));
        }

    }


    public abstract void doProcess(Task task, ICallBack callback);
}
