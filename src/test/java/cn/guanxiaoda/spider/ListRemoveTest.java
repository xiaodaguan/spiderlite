package cn.guanxiaoda.spider;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/5/2
 */
public class ListRemoveTest {


    @Test
    public void linkedListRm() {
        List list = Lists.newArrayList();
        list.addAll(IntStream.range(0, 10).boxed().collect(Collectors.toList()));
        System.out.println(list);
        list.addAll(Arrays.asList(11, 12, 13, 15, 14));
        if (list.size() > 10) {
            list = (List) list.stream().skip(list.size() - 10).collect(Collectors.toList());
        }
        System.out.println(list);
    }

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
