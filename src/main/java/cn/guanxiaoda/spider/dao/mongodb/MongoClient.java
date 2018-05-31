package cn.guanxiaoda.spider.dao.mongodb;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

/**
 * @author guanxiaoda
 * @date 2018/5/11
 */
@Component(value = "mongoClient")
@ConfigurationProperties
@Slf4j
public class MongoClient implements IMongoDbClient {

    private com.mongodb.MongoClient client;
    @Value("${custom.mongodb.host}")
    private String host;
    @Value("${custom.mongodb.port}")
    private int port;
    @Value("${custom.mongodb.db}")
    private String db;
    @Value("${custom.mongodb.user}")
    private String user;
    @Value("${custom.mongodb.pass}")
    private String pass;

    @Autowired
    public void init() {
        client = new com.mongodb.MongoClient(
                Lists.newArrayList(new ServerAddress(host, port)),
                MongoCredential.createCredential(user, db, pass.toCharArray()),
                new MongoClientOptions.Builder()
                        .connectTimeout(10000)
                        .build());

    }

    @Override
    public void save(String collection, Map<String, Object> item) {
        genUniqueIdIfNotExist(item);
        try {
            Document doc = findDocByItem(collection, item);
            if (doc == null) {
                client.getDatabase(db).getCollection(collection).insertOne(new Document(item));
                log.info("item saved: {} <- {}", collection, item);
            } else {
                update(collection, item);
            }
        } catch (MongoWriteException e) {
            log.error("save item failure: msg={},{} <- item={}", e.getMessage(), collection, item);
        }
    }

    @Override
    public void save(String collection, List<Map<String, Object>> items) {
        items.parallelStream().forEach(item -> save(collection, item));
    }

    @Override
    public List<String> findAllCollectionNames() {
        return StreamSupport.stream(client.getDatabase(db).listCollectionNames().spliterator(), true)
                .collect(Collectors.toList());
    }

    @Override
    public Document findDocByItem(String collection, Map<String, Object> item) {
        genUniqueIdIfNotExist(item);
        try {
            return client.getDatabase(db).getCollection(collection).find(eq("_id", item.get("_id"))).first();
        } catch (Exception e) {
            log.error("find doc failure: msg={}, coll={}, doc={}", e.getMessage(), collection, item);
        }
        return null;
    }

    @Override
    public List<Document> findDocByFieldLike(String collection, String field, Object value) {
        return StreamSupport.stream(client.getDatabase(db).getCollection(collection)
                        .find(regex(field, ".*" + value + ".*")).spliterator(),
                true)
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> findAllDocs(String collection) {
        return StreamSupport.stream(client.getDatabase(db).getCollection(collection).find().spliterator(), true)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String collection, Map<String, Object> item) {
        genUniqueIdIfNotExist(item);
        client.getDatabase(db).getCollection(collection).updateOne(eq("_id", item.get("_id")), new Document("$set", new Document(item)));
        log.info("item updated: {} <- {}", collection, item);
    }

    private void genUniqueIdIfNotExist(Map<String, Object> item) {
        if (item.containsKey("_id")) {
            return;
        }
        if (item.containsKey("unique_keys")) {
            item.put("_id",
                    Hashing.sha256().hashString(Arrays.stream(StringUtils.split(String.valueOf(item.get("unique_keys")), ','))
                            .map(item::get).map(String::valueOf).collect(Collectors.joining(",")), Charset.defaultCharset()).toString()
            );
        } else {
            if (item.containsKey("uniqueKey")) {
                item.put("_id", Hashing.sha256().hashString(String.valueOf(item.get("uniqueKey")), Charset.defaultCharset()).toString());
            } else if (item.containsKey("name")) {
                item.put("_id", Hashing.sha256().hashString(String.valueOf(item.get("name")), Charset.defaultCharset()).toString());
            } else if (item.containsKey("title")) {
                item.put("_id", Hashing.sha256().hashString(String.valueOf(item.get("title")), Charset.defaultCharset()).toString());
            } else {
                log.warn("there is no field named [uniqueKey] or [name] or [title], item _id might not be unique.");
                item.put("_id", Hashing.sha256().hashString(item.toString(), Charset.defaultCharset()).toString());
            }
        }
    }
}
