package cn.guanxiaoda.spider.components.vert.concrete.wx;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.Lists;
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
@Component(value = "wxPersister")
@Slf4j
public class Persister implements IProcessor<Task> {

    @Autowired @Qualifier("mongoClient") IMongoDbClient mongoDbClient;

    @Override
    public void process(Task task) {
        List parsed = Optional.ofNullable(task.getCtx()).map(ctx -> ctx.get("parsed"))
                .map(obj -> (List<Map<String, Object>>) obj)
                .orElse(Lists.newArrayList());
        String collection = Optional.ofNullable(task.getCtx())
                .map(ctx -> ctx.get("collection"))
                .map(String::valueOf)
                .orElse("default");
        mongoDbClient.save(collection, parsed);

        task.setStage("persisted");
    }
}
