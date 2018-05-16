package cn.guanxiaoda.spider.utils;

import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Slf4j
public class RetryUtils {


    private RetryUtils() {}

    public static <T> T retry(Callable<T> callable) {
        try {
            return RetryerBuilder.<T>newBuilder()
                    .retryIfException()
                    .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                    .withWaitStrategy(WaitStrategies.randomWait(500, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS))
                    .build()
                    .call(callable);
        } catch (Exception e) {
            log.error("retry failure", e);
        }
        return null;
    }

}
