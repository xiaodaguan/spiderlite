package cn.guanxiaoda.spider.proxy;

import org.apache.http.HttpHost;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
public interface IProxyManager {
    HttpHost randomGetOne();
    void refresh();
}
