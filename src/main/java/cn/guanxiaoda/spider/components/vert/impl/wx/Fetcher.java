package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import cn.guanxiaoda.spider.utils.RetryUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/4/17
 */
@Component(value = "wxFetcher")
@Slf4j
public class Fetcher implements IProcessor<Task> {

    private static RateLimiter rl = RateLimiter.create(0.1);
    private static Map<String, String> headers = Maps.newHashMap(
            ImmutableMap.<String, String>builder()
                    .put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .put("Accept-Encoding", "gzip, deflate, br")
                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                    .put("Cache-Control", "max-age=0")
                    .put("Connection", "keep-alive")
                    .put("Host", "mp.weixin.qq.com")
                    .put("Upgrade-Insecure-Requests", "1")
                    .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3409.0 Safari/537.36")
                    .build()
    );

    @Override
    public Task process(Task task) {
        log.error(JSON.toJSONString(task));
        int pageNo = Optional.of(task.getCtx()).map(ctx -> ctx.get("pageNo")).map(Integer.class::cast).orElse(1);
        String url = Optional.of(task.getCtx()).map(ctx -> ctx.get("url")).map(String::valueOf)
                .map(tmp -> tmp.replace("{begin}", String.valueOf((pageNo - 1) * 10)))
                .orElse("");

        Unirest.setHttpClient(ClientPool.getDefaultClient());
        rl.acquire();
        HttpResponse<String> response = RetryUtils.retry(() -> Unirest.get(url)
                .header("Cookie", "noticeLoginFlag=1; ua_id=sTPaxE9j3XNEt5jlAAAAAKYAXptzCThn85jJJQN43Uk=; mm_lang=en_US; pgv_pvi=4797978624; pgv_si=s9122960384; uuid=6b4b04778ae4de1780b21897151d0a01; ticket=43fda34901e48dc104dae47cdbf6bca2ecaf2d2f; ticket_id=gh_eecd327a369c; cert=dftBLBNGRhVasKzCSFF1olmqMij1ObXC; data_bizuin=3595588900; bizuin=3550833854; data_ticket=0h2qgHeaLO+w+L7HR5TE8Munr6FtvVzXmCH68GUSzwilcbbaHeJTcCoSwlrpKkWa; slave_sid=SGI2RG5aWklnNnhfNzNQOXlERFNHU01FY2RnaFFKMHFWQWJvN1pNZ3B5a3hQbGlIMnZIT0cyWXdVbnJPNV91blRPbXdiTzlLamxBaFZGSTVJbzBvdGgwc3FiZ2RYM0plZjdnRFVxVGtzaDJXeGFISjJPZHBtY3lJN2dTaVRlRHY4bFo3Z2NIanZtUHVZb1c0; slave_user=gh_eecd327a369c; xid=7d0ca9505a687750380072a9229b92f7; openid2ticket_oJFVQ1kBxXq5xBn4vhhUaIedXTao=bPh9/FsND+IsEFflzngOaTp2yBF0UFaMdM7UYixJdAc=")
                .headers(headers)
                .asString()
        );

        Optional.ofNullable(response).ifPresent(resp -> {
            if (resp.getStatus() != HttpStatus.SC_OK) {
                return;
            }
            task.getCtx().put("fetched", response.getBody());
            task.setStage("fetched");
        });


        return task;
    }

}
