package cn.guanxiaoda.spider;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/4/20
 */
public class SimpleTest {


    @Test
    public void testUudi() {
        IntStream.range(0, 10).parallel().mapToObj(i -> UUID.randomUUID().toString()).forEach(System.out::println);
    }

    @Test
    public void testOptional() {
        Object a = null;
        Object b = "9.5";
        Object c = "";

        Double x = Optional.ofNullable(a)
                .map(Object::toString)
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(Double::parseDouble)
                .orElseGet((Supplier<Double>) () -> 0d);
        System.out.println(x);
    }


}
