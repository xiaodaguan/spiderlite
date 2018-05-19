package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseAsyncProcessor implements IProcessor<Task> {


    @Override
    public void process(Task task, ICallBack callback) {

        doProcess(task, callback);

    }


    public abstract void doProcess(Task task, ICallBack callback);
}
