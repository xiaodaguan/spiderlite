package cn.guanxiaoda.spider.proxy;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
@EnableScheduling
@ConfigurationProperties
public class GobanjiaProxyManager implements IProxyManager {


    private static List<String> proxyContainer;
    private static CloseableHttpClient client = HttpClientBuilder.create().build();
    private static RateLimiter rl = RateLimiter.create(1);
    @Value("${proxy.vendor.url}")
    public String url;

    @Override
    public HttpHost randomGetOneHttpHost() {
        if (proxyContainer.size() == 0) {
            return null;
        }
        String proxy = proxyContainer.get(new Random().nextInt(proxyContainer.size()));
        log.info("[random proxy]: {}", proxy);
        return Optional.of(proxy)
                .map(str -> str.split(":"))
                .map(arr -> new HttpHost(arr[0], Integer.parseInt(arr[1])))
                .orElse(null);
    }

    @Override
    public SocketAddress randomGetOneAddress() {
        if (proxyContainer.size() == 0) {
            return null;
        }
        String proxy = proxyContainer.get(new Random().nextInt(proxyContainer.size()));
        log.info("[random proxy]: {}", JSON.toJSONString(proxy));
        return Optional.of(proxy)
                .map(str -> str.split(":"))
                .map(arr -> new InetSocketAddress(arr[0], Integer.parseInt(arr[1])))
                .orElse(null);
    }

    @Override
    @Scheduled(fixedRate = 1500)
    public void refresh() {
        if (proxyContainer == null) {
            proxyContainer = Lists.newArrayList();
        }

        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            rl.acquire();
            response = client.execute(get);
            if (200 != response.getStatusLine().getStatusCode()) {
                log.warn("didn't get proxy");
                return;
            }
            proxyContainer.addAll(
                    Optional.of(response)
                            .map(resp -> {
                                try {
                                    return EntityUtils.toString(resp.getEntity());
                                } catch (IOException e) {
                                    log.error("consume entity failure", e);
                                    return null;
                                }
                            })
                            .map(body -> StringUtils.split(body, "\n"))
                            .map(Arrays::asList)
                            .map(list -> list.stream()
                                    .peek(str -> {
                                        if (str.contains("请控制好请求频率")) {
                                            log.error("request proxy vendor failure");
                                        }
                                    })
                                    .filter(str -> StringUtils.split(str, ":").length == 2)
                                    .collect(Collectors.toList())
                            )
                            .orElse(Lists.newArrayList())

            );

        } catch (Exception e) {
            log.error("refresh proxy failure.", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized (this) {
            if (proxyContainer.size() > 10) {
                proxyContainer = proxyContainer.stream().skip(proxyContainer.size() - 5).collect(Collectors.toList());
            }
        }
    }

    @PostConstruct
    public void init() {
        refresh();
    }
}
