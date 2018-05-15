package cn.guanxiaoda.spider.components.vert;

import cn.guanxiaoda.spider.models.Task;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
public interface IProcessor<T> {
    void process(T t, ICallBack callback);

    void doProcess(Task task);
}
