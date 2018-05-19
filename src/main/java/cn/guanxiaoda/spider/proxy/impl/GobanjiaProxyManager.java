package cn.guanxiaoda.spider.proxy.impl;

import cn.guanxiaoda.spider.proxy.BaseProxyManager;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
@Component(value = "gobanjiaProxyManager")
@Slf4j
@EnableScheduling
@ConfigurationProperties
public class GobanjiaProxyManager extends BaseProxyManager {


    @Override
    public List<String> getIpPortList(String content) {
        return Optional.ofNullable(content)
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
                .orElse(Lists.newArrayList());
    }

}
