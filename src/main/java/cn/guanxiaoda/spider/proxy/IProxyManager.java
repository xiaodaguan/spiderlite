package cn.guanxiaoda.spider.proxy;

import org.apache.http.HttpHost;

import java.net.Proxy;
import java.net.SocketAddress;
import java.util.List;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
public interface IProxyManager {
    HttpHost randomGetOneHttpHost();

    SocketAddress randomGetOneAddress();

    List<String> getIpPortList(String content);

    void removeProxy(String ipPort);

    void recordProxyFailure(String ipPort);

}
