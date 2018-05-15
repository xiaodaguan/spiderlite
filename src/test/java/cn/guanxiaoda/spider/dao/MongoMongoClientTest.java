package cn.guanxiaoda.spider.dao;

import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/5/11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoMongoClientTest {

    @Autowired @Qualifier("mongoClient") IMongoDbClient client;

    @Test
    public void testSave() {
        Document doc = new Document(Maps.newHashMap(ImmutableMap.<String, Object>builder().put("_id", "aaabbbccc").put("name", "gxd").put("age", 30).build()));

        client.save("test", doc);

        client.save("test1", IntStream.range(0, 5).mapToObj(i -> new Document(Maps.newHashMap(
                ImmutableMap.<String, Object>builder()
                        .put("_id", "item_" + i)
                        .put("name", "person" + i)
                        .build()
        ))).collect(Collectors.toList()));
    }
}
