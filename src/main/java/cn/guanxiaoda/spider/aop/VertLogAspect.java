package cn.guanxiaoda.spider.aop;

import cn.guanxiaoda.spider.models.Task;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/5/10
 */
@Aspect
@Component
@Slf4j
public class VertLogAspect {

    @Pointcut("execution(public cn.guanxiaoda.spider.models.Task cn.guanxiaoda.spider.components..*.process(..))")
    public void log() {

    }

    @Before("log()")
    public void logBefore(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        for (Object arg : args) {
//            if (arg instanceof Task) {
//
//                log.info("before {} process: stage={}, task={}",
//                        joinPoint.getTarget().getClass().getSimpleName(),
//                        Optional.of(arg)
//                                .map(Task.class::cast)
//                                .map(Task::getStage).orElse("none")
//                        , JSON.toJSONString(arg));
//            }
//        }
    }

    @After("log()")
    public void logAfter(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        for (Object arg : args) {
//            if (arg instanceof Task) {
//
//                log.info("after {} process: stage={}, task={}",
//                        joinPoint.getTarget().getClass().getSimpleName(),
//                        Optional.of(arg)
//                                .map(Task.class::cast)
//                                .map(Task::getStage).orElse("none")
//                        , JSON.toJSONString(arg));
//            }
//        }
    }

    @AfterThrowing(pointcut = "log()", throwing = "e")
    public void afterThrowing(Throwable e) {
//        log.error("processing failure. ", e);
    }
}
