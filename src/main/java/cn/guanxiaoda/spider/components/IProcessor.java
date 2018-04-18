package cn.guanxiaoda.spider.components;

/**
 * @author guanxiaoda
 * @date 2018/4/18
 */
public interface IProcessor<T> {
    T process(T t);
}
