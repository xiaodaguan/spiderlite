package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.conf.FastJsonConf;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseSyncProcessor extends BaseProcessor {


    @Override
    public void process(Task task, ICallBack callback) {


        try {
            if (doProcess(task)) {

                try {
                    callback.call(task);
                } catch (Exception e) {
                    log.error("callback failure", e);
                    retry(task);
                }
            } else {
                log.error("process failure, task={}", JSON.toJSONString(task, FastJsonConf.filter));
            }
        } catch (Exception e) {
            log.error("async processor process failure task={}", JSON.toJSONString(task, FastJsonConf.filter));
        }
    }

    protected abstract boolean doProcess(Task task);
}
