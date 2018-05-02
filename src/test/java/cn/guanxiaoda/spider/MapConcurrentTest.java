package cn.guanxiaoda.spider;

import org.junit.Test;

import java.util.HashMap;

/**
 * @author guanxiaoda
 * @date 2018/5/2
 */
public class MapConcurrentTest {
    static HashMap<Integer, String> map = new HashMap<>(2);


    @Test
    public void concurrentTest() {

        map.put(5, "C");

        new Thread(new ThreadGroup("mythreads"), new Runnable() {
            @Override
            public void run() {
                map.put(7, "B");
            }
        }, "thread1").start();
        new Thread(new ThreadGroup("mythreads"), new Runnable() {
            @Override
            public void run() {
                map.put(3, "A");
            }
        }, "thread2").start();


        System.out.println("aaaaa");
    }
}
