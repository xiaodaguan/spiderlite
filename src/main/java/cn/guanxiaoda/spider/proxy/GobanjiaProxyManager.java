package cn.guanxiaoda.spider.proxy;

import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.utils.RetryUtils;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
@Component(value = "gobanjiaProxyManager")
@Slf4j
@ConfigurationProperties
public class GobanjiaProxyManager implements IProxyManager {


    private static List<HttpHost> proxyContainer;

    @Value("${proxy.vendor.url}")
    public String url;

    @Override
    public HttpHost randomGetOne() {
        return proxyContainer.get(new Random().nextInt(proxyContainer.size()));
    }

    @Override
    @Synchronized
    public void refresh() {
        if (proxyContainer == null) {
            proxyContainer = Lists.newArrayList();
        }

        Unirest.setHttpClient(ClientPool.getDefaultClient());

        proxyContainer = Optional.ofNullable(RetryUtils.retry(() -> Unirest.get(url).asString()))
                .map(HttpResponse::getBody)
                .map(body -> StringUtils.split(body, "\n"))
                .map(Arrays::asList)
                .orElse(Lists.newArrayList())
                .stream()
                .map(str -> StringUtils.split(str, "\t"))
                .filter(array -> array.length == 2)
                .map(array -> new HttpHost(array[0], Integer.parseInt(array[1])))
                .collect(Collectors.toList());
        log.info("refresh {} proxies", proxyContainer.size());
    }

    @PostConstruct
    public void init() {
        refresh();
    }
}
