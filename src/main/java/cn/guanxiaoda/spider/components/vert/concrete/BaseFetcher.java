package cn.guanxiaoda.spider.components.vert.concrete;

import cn.guanxiaoda.spider.components.vert.ICallBack;
import cn.guanxiaoda.spider.http.ClientPool;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@Slf4j
public abstract class BaseFetcher extends BaseProcessor {
    protected static RateLimiter rl = RateLimiter.create(5);
    protected @Autowired ClientPool clientPool;
    ExecutorService pool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("fetcher-pool-%d").build());

    @Override
    public void process(Task task, ICallBack callback) {
        pool.submit(() -> {

            doProcess(task);

            try {
                callback.call(task);
                monitor.tell(task);
            } catch (Exception e) {
                log.error("callback failure", e);
            }
        });
    }

    @Override
    public abstract void doProcess(Task task);
}
