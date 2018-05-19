package cn.guanxiaoda.spider.monitor;

import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/5/14
 */
@Component("taskMonitor")
@EnableScheduling
@Slf4j
public class TaskMonitor {

    private static final String MONITOR_COLLECTION = "monitor_collection";
    private static final String TOTAL_ID = "total";
    private static final String DETAIL_ID = "detail";
    /**
     * task name, total count
     */
    private static ConcurrentMap<String, AtomicInteger> totalCount;
    /**
     * task name, stage, count
     */
    private static ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> detailCount;
    private static List<Long> crawlTimes = new ArrayList<>();
    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoDbClient;

    public void tell(Task task) {

        init();
        crawlTimes.add(System.currentTimeMillis());


        if (!totalCount.containsKey(task.getName())) {
            totalCount.put(task.getName(), new AtomicInteger());
        }
        if (!detailCount.containsKey(task.getName())) {
            detailCount.put(task.getName(), Maps.newConcurrentMap());
        }
        if (!detailCount.get(task.getName()).containsKey(task.getStage())) {
            detailCount.get(task.getName()).put(task.getStage(), new AtomicInteger());
        }
        totalCount.get(task.getName()).incrementAndGet();
        detailCount.get(task.getName()).get(task.getStage()).getAndIncrement();
    }

    @PostConstruct
    private void init() {
        if (MapUtils.isEmpty(totalCount) || MapUtils.isEmpty(detailCount)) {
//            load();
            if (MapUtils.isEmpty(totalCount) || MapUtils.isEmpty(detailCount)) {
                create();
            }
        }
    }

    private void create() {
        totalCount = Maps.newConcurrentMap();
        detailCount = Maps.newConcurrentMap();
    }

    public String getTotalStat() {
        return JSON.toJSONString(totalCount);
    }

    public String getDetailStat() {
        return JSON.toJSONString(detailCount);
    }

    private void load() {
        totalCount = Maps.newConcurrentMap();
        Optional.ofNullable(mongoDbClient.findDocById(MONITOR_COLLECTION, Maps.newHashMap(
                ImmutableMap.<String, Object>builder()
                        .put("_id", TOTAL_ID)
                        .build())))
                .map(doc -> doc.get("map"))
                .map(Map.class::cast)
                .ifPresent(map -> map
                        .forEach((k, v) -> totalCount.put((String) k, new AtomicInteger((Integer) v))));

        detailCount = Maps.newConcurrentMap();
        Optional.ofNullable(mongoDbClient.findDocById(MONITOR_COLLECTION, Maps.newHashMap(
                ImmutableMap.<String, Object>builder()
                        .put("_id", DETAIL_ID)
                        .build())))
                .map(doc -> doc.get("map"))
                .map(Map.class::cast)
                .ifPresent(map -> map.forEach((k, v) -> {
                    String spider = (String) k;
                    detailCount.put(spider, Maps.newConcurrentMap());
                    ((Map<String, Integer>) v)
                            .forEach((stage, count) -> detailCount.get(spider).put(stage, new AtomicInteger(count)));
                }));

    }

    @Scheduled(fixedRate = 1000 * 5)
    private void persist() {

        init();
        synchronized (TaskMonitor.class) {
            crawlTimes = crawlTimes.parallelStream().filter(time -> time >= System.currentTimeMillis() - 1000 * 60).collect(Collectors.toList());
        }
        log.info("real time crawling speed: {}/min", crawlTimes.size());
//        save();
    }

    private void save() {
        mongoDbClient.save(MONITOR_COLLECTION, Maps.newHashMap(
                ImmutableMap.<String, Object>builder().put("_id", TOTAL_ID)
                        .put("map", totalCount)
                        .build()));
        mongoDbClient.save(MONITOR_COLLECTION, Maps.newHashMap(
                ImmutableMap.<String, Object>builder().put("_id", DETAIL_ID)
                        .put("map", detailCount)
                        .build()));
    }
}