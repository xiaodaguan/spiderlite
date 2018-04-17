package cn.guanxiaoda.spider.flow;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
public interface IParser<T> {
    T parse(T t);
}
