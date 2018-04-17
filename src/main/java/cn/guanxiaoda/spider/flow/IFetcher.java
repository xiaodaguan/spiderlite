package cn.guanxiaoda.spider.flow;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
public interface IFetcher<T> {
    T fetch(T t);
}
