package cn.guanxiaoda.spider;

import lombok.Builder;
import lombok.ToString;
import org.junit.Test;

import java.util.Optional;

/**
 * @author guanxiaoda
 * @date 2018/6/5
 */
public class OptionalTest {

    @Test
    public void test(){
        Person gxd = Optional.ofNullable(createPerson()).get();
        System.out.println(gxd);
    }

    private Person createPerson() {
        throw new RuntimeException("create failure");
//        return Person.builder().name("gxd").age(13).build();
    }
}

@Builder
@ToString
class Person{
    String name;
    Integer age;
}