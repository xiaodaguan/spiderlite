package cn.guanxiaoda.spider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanxiaoda
 * @date 2018/5/2
 */
public class ListRemoveTest {


    @Test
    public void testRemove1() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        System.out.println(list);
        for (String item : list) {
            if ("1".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println(list);
    }

    @Test
    public void testRemove2() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
//        list.add("2");
//        list.add("3");
//        list.add("3");
        list.add("3");
        System.out.println(list);
        for (String item : list) {
            if ("2".equals(item)) {
                list.remove(item);
            }
            System.out.println(list);
        }
        for (String item : list) {
            if ("3".equals(item)) {
                list.remove(item);
            }
            System.out.println(list);
        }
    }

    @Test
    public void testReverseRemove() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("2");
        list.add("3");
        list.add("3");
        list.add("3");
        System.out.println(list);
        for (int i = list.size() - 1; i >= 0; i--) {
            String item = list.get(i);
            if ("3".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println(list);
        for (int i = list.size() - 1; i >= 0; i--) {
            String item = list.get(i);
            if ("2".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println(list);
        for (int i = list.size() - 1; i >= 0; i--) {
            String item = list.get(i);
            if ("1".equals(item)) {
                list.remove(item);
            }
        }
        System.out.println(list);
    }
}
