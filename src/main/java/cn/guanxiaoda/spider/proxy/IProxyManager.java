package cn.guanxiaoda.spider.proxy;

import org.apache.http.HttpHost;

import java.net.SocketAddress;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
public interface IProxyManager {
    HttpHost randomGetOneHttpHost();
    SocketAddress randomGetOneAddress();
    void refresh();
}
