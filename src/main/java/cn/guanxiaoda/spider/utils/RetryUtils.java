package cn.guanxiaoda.spider.utils;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
public class RetryUtils {


    private RetryUtils() {}

    public static <T> T retry(Callable<T> callable) {
        try {
            return RetryerBuilder.<T>newBuilder()
                    .retryIfException()
                    .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                    .withWaitStrategy(WaitStrategies.randomWait(1000, TimeUnit.MILLISECONDS, 15000, TimeUnit.MILLISECONDS))
                    .build().call(callable);
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
        }
        return null;
    }

}
