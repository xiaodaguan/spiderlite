package cn.guanxiaoda.spider;

import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author guanxiaoda
 * @date 2018/6/1
 */
public class ComparatorTest {

    @Test
    public void test() {

        List<People> peoples = new Random().ints(0, 100).limit(100)

                .mapToObj(i -> People.builder().age(i).name("person_" + i).build())
                .sorted(Comparator.comparingInt(People::getAge))
                .limit(30)
                .collect(Collectors.toList());
        System.out.println(peoples);
    }


}

@Builder
@Data
class People {
    String name;
    Integer age;
}