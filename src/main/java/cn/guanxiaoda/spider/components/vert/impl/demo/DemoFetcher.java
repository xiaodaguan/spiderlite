package cn.guanxiaoda.spider.components.vert.impl.demo;

import cn.guanxiaoda.spider.components.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component
public class DemoFetcher implements IProcessor<Task> {
    @Override
    public Task process(Task task) {
        return task.setFetched(task.getUrl() + "(fetched)");
    }

}