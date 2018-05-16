package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "itemPersister")
@Slf4j
public class ItemPersister extends BaseProcessor {

    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoDbClient;

    @Override
    public void doProcess(Task task) {
        Object parsed = Optional.ofNullable(task.getCtx()).map(ctx -> ctx.get("parsed")).orElse(null);

        String collection = Optional.ofNullable(task.getCtx())
                .map(ctx -> ctx.get("collection"))
                .map(String::valueOf)
                .orElse("default");

        if (parsed instanceof List) {
            mongoDbClient.save(collection, (List) parsed);
            task.setStage("persisted");
        } else if (parsed instanceof Map) {
            mongoDbClient.save(collection, (Map) parsed);
            task.setStage("persisted");
        } else {
            log.error("illegal parse result type, parsed={}", parsed);
        }

    }
}
