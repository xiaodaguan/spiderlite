package cn.guanxiaoda.spider.flow.impl.demo;

import cn.guanxiaoda.spider.flow.IParser;
import cn.guanxiaoda.spider.models.Task;
import org.springframework.stereotype.Component;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component
public class DemoParser implements IParser<Task> {
    @Override
    public Task parse(Task task) {
        return task.setParsed(task.getFetched() + "(parsed)");
    }
}
