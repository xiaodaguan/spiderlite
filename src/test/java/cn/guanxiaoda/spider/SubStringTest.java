package cn.guanxiaoda.spider;

import org.junit.Test;

/**
 * @author guanxiaoda
 * @date 2018/4/28
 */
public class SubStringTest {


    @Test
    public void test() {
        String a = "s";
        System.out.println(a.length());
        String b = a.substring(1);
        System.out.println("b:"+b);
    }
}
