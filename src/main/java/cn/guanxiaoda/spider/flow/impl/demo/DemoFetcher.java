package cn.guanxiaoda.spider.flow.impl.demo;

import cn.guanxiaoda.spider.flow.IFetcher;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component
public class DemoFetcher implements IFetcher<Task> {
    @Override
    public Task fetch(Task task) {
        return task.setFetched(task.getUrl() + "(fetched)");
    }
}
