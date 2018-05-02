package cn.guanxiaoda.spider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanxiaoda
 * @date 2018/5/2
 */
public class PECSTest {

    @Test
    public void test(){
        List<? extends Animal> animals = new ArrayList<>();
//        animals.add(new Horse());
//        animals.add(new Animal());
        Animal anAnimal = animals.get(0);

        List<? super Animal> horses = new ArrayList<>();
        horses.add(new Horse());
        horses.add(new Animal());
//        horses.add(new Object());
        Animal animal = (Animal) horses.get(0);
        Object obj = horses.get(0);

    }

    public class Animal{

    }

    public class Horse extends Animal{

    }
}
