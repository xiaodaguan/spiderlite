package cn.guanxiaoda.spider.components.vert.concrete.lagou.itemdetail;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import im.nll.data.extractor.Extractors;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static im.nll.data.extractor.Extractors.selector;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "lagouDetailParser")
public class Parser implements IProcessor<Task> {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void process(Task task) {

        Optional.of(task.getCtx())
                .map(ctx -> ctx.get("fetched"))
                .map(String::valueOf)
                .map(body -> {
                            Map<String, String> item = Extractors.on(body)
                                    .extract("jobTitle", selector("div.postitle>.title.text"))
                                    .extract("salary", selector("div.detail>div.items .item.salary.text"))
                                    .extract("workAddress", selector("div.detail>div.items .item.workaddress.text"))
                                    .extract("jobnature", selector("div.detail>div.items .item.jobnature.text"))
                                    .extract("workyear", selector("div.detail>div.items .item.workyear.text"))
                                    .extract("education", selector("div.detail>div.items .item.education.text"))
                                    .extract("temptation", selector("div.detail>div.temptation.text"))
                                    .extract("companyName", selector("div.company>div.desc .title.text"))
                                    .extract("companyInfo", selector("div.company>div.desc .info.text"))
                                    .extract("desc", selector("div.positiondesc .content.text"))
                                    .asMap();
                            Optional.of(task)
                                    .map(Task::getCtx)
                                    .ifPresent(ctx -> {
                                        item.put("uniqueKey", String.valueOf(ctx.get("uniqueKey")));
                                        item.put("positionName", String.valueOf(ctx.get("positionName")));
                                        item.put("city", String.valueOf(ctx.get("city")));
                                        item.put("salary", String.valueOf(ctx.get("salary")));
                                        item.put("companyId", String.valueOf(ctx.get("companyId")));
                                        item.put("companyLogo", String.valueOf(ctx.get("companyLogo")));
                                        item.put("companyName", String.valueOf(ctx.get("companyName")));
                                        item.put("companyFullName", String.valueOf(ctx.get("companyFullName")));
                                        item.put("crawlTime", LocalDateTime.now().format(dtf));
                                    });

                            return item;
                        }

                ).ifPresent(item -> {
            task.getCtx().put("parsed", item);
            task.setStage("parsed");
        });
    }
}
