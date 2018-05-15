package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "countPersister")
@Slf4j
public class CountPersister implements IProcessor<Task> {

    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoDbClient;

    @Override
    public void process(Task task) {
        Object parsed = Optional.ofNullable(task.getCtx())
                .map(ctx -> ctx.get("parsed"))
                .orElse(0);
        String cityId = Optional.ofNullable(task.getCtx())
                .map(ctx -> ctx.get("cityId"))
                .map(String::valueOf)
                .orElse("");
        String collection = Optional.ofNullable(task.getCtx())
                .map(ctx -> ctx.get("collection"))
                .map(String::valueOf)
                .orElse("default");

        if (parsed instanceof Integer) {
            mongoDbClient.save(collection, Maps.newHashMap(
                    ImmutableMap.<String, Object>builder()
                            .put("name", cityId)
                            .put("count", parsed)
                            .build()));
        } else {
            log.error("illegal parse result type, parsed={}", parsed);
        }

        task.setStage("persisted");
    }
}
