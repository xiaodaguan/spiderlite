package cn.guanxiaoda.spider.components.apps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.util.concurrent.RateLimiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import im.nll.data.extractor.Extractors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static im.nll.data.extractor.Extractors.selector;

/**
 * @author guanxiaoda
 * @date 2018/5/8
 */
@Slf4j
@ConfigurationProperties
public class MafengwoMddCityList {

    private static List<String> PROXY_LIST = Arrays.asList(

    );
    private static Retryer<HttpResponse<String>> retryer = RetryerBuilder.<HttpResponse<String>>newBuilder()
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(10))
            .build();
    private static RateLimiter rateLimiter = RateLimiter.create(5);

    private static HttpHost randomProxy() {
        String proxy = PROXY_LIST.get(new Random().nextInt(PROXY_LIST.size()));
        return new HttpHost(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1]));
    }

    public static void main(String[] args) {

        MafengwoMddCityList app = new MafengwoMddCityList();
        List<Map<String, String>> allResults = IntStream.range(0, 423).mapToObj(i -> {
            log.info("filtering... {}", i);
            return app.filterIds(i);
        }).filter(Objects::nonNull).filter(CollectionUtils::isNotEmpty).flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, List<Map<String, String>>> groupResult = allResults.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(map -> map.get("type")));
        log.info(JSON.toJSONString(groupResult));
        groupResult.get("city").forEach(m -> log.info("id={}, name={}", m.get("id"), m.get("name")));
    }

    private List<Map<String, String>> filterIds(int pageNo) {
        HttpResponse<String> response = null;

        try {
            response = retryer.call(() -> {
                Unirest.setProxy(randomProxy());
                rateLimiter.acquire();
                return Unirest.post("http://www.mafengwo.cn/mdd/base/list/pagedata_citylist")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .body("mddid=21536&page=" + pageNo)
                        .asString();

            });
        } catch (Exception e) {
            log.error("request list failure, pageNo={}, msg={}", pageNo, e.getMessage());
        } finally {
            try {
                Unirest.shutdown();
            } catch (IOException e) {
                log.error("close http client failure", e);
            }
        }

        if (response == null || response.getStatus() != 200 || response.getBody() == null) {
            return null;
        }
        JSONObject jObj = JSON.parseObject(response.getBody());
        String html = jObj.getString("list");

        List<Map<String, String>> result = Extractors.on(html).split(selector("li.item.html"))
                .extract("id", selector("div.img>a[data-id].attr(data-id)"))
                .extract("href", selector("div.img>a[data-id].attr(data-id)"))
                .filter(value -> "http://www.mafengwo.cn/travel-scenic-spot/mafengwo/" + value + ".html")
                .asMapList();
        return result.stream().peek(
                map -> {
                    String id = map.get("id");
                    String url = map.get("href");

                    HttpResponse<String> detailResp = null;
                    try {
                        detailResp = retryer.call(() -> {
                            Unirest.setProxy(randomProxy());
                            rateLimiter.acquire();
                            return Unirest.get(url).asString();
                        });
                    } catch (Exception e) {
                        log.error("request detail failure, id={}, errMsg={}", id, e.getMessage());
                    } finally {
                        try {
                            Unirest.shutdown();
                        } catch (IOException e) {
                            log.error("close http client failure", e);
                        }
                    }

                    if (detailResp == null) {
                        return;
                    }

                    if (detailResp.getStatus() == 200) {
                        String detailHtml = detailResp.getBody();
                        if (Extractors.on(detailHtml).extract(selector("link[rel='canonical'].attr(href)")).asString().contains("travel-scenic-spot/mafengwo")) {
                            String name = Extractors.on(detailHtml).extract(selector("div.title>h1.text")).asString();
                            map.put("type", "city");
                            map.put("name", name);
                        } else {
                            map.put("type", "poi");
                        }
                    } else if (detailResp.getStatus() >= 301 && detailResp.getStatus() < 400) {
                        map.put("type", "poi");
                    } else {
                        map.put("type", "other");
                    }
                }
        ).collect(Collectors.toList());
    }
}
