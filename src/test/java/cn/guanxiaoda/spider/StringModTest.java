package cn.guanxiaoda.spider;

import org.junit.Test;

/**
 * @author guanxiaoda
 * @date 2018/4/28
 */
public class StringModTest {

    private static final int loops = 10000;

    @Test
    public void plusTest() {
        long start = System.nanoTime();
        String a = "str1";
        for (int i = 0; i < loops; i++) {
            a += "str2";
        }
        long end = System.nanoTime();
        System.out.println(end - start);

//        System.out.println(a);
    }

    @Test
    public void builderTest() {
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder("str1");
        for (int i = 0; i < loops; i++) {
            sb.append("str2");
        }
        String b = sb.toString();
//        System.out.println(b);
        long end = System.nanoTime();
        System.out.println(end - start);
    }


    @Test
    public void concatTest() {
        long start = System.nanoTime();
        String a = "str1";
        for (int i = 0; i < loops; i++) {
            a = a.concat("str2");
        }
//        System.out.println(a);
        long end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void formatTest() {
        long start = System.nanoTime();
        String a = "str1";
        for (int i = 0; i < loops; i++) {
            a = String.format("%s%s", a, "str2");
        }
//        System.out.println(a);
        long end = System.nanoTime();
        System.out.println(end - start);
    }


    @Test
    public void createString() {
        long start = System.nanoTime();
        for (int i = 0; i < loops; i++) {
            String a = "str1";
        }
        long end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void createSB() {
        long start = System.nanoTime();
        for (int i = 0; i < loops; i++) {
            StringBuilder sb = new StringBuilder("str2");
        }
        long end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void simplePlus() {
        long start = System.nanoTime();
        String a = "str1" + "str2";
        long end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void simpleBuilder() {
        long start = System.nanoTime();
        String a = new StringBuilder("str1").append("str2").toString();
        long end = System.nanoTime();
        System.out.println(end - start);
    }

    @Test
    public void simpleConcat() {
        long start = System.nanoTime();
        String a = "str1".concat("str2");
        long end = System.nanoTime();
        System.out.println(end - start);
    }


    @Test
    public void simpleFormat() {
        long start = System.nanoTime();
        String a = String.format("%s%s", "str1", "str2");
        long end = System.nanoTime();
        System.out.println(end - start);
    }

}
