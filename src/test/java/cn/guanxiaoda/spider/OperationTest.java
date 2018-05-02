package cn.guanxiaoda.spider;

import org.junit.Test;

/**
 * @author guanxiaoda
 * @date 2018/5/2
 */
public class OperationTest {


    @Test
    public void orEqTest() {
        int cap = 5;
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        n = (n < 0) ? 1 : (n >= 1 << 30) ? 1 << 30 : n + 1;
        System.out.println(n);
    }
}
