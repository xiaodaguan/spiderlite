package cn.guanxiaoda.spider.dao.mongodb;

import org.bson.Document;

import java.util.List;
import java.util.Map;

/**
 * @author guanxiaoda
 * @date 2018/5/11
 */
public interface IMongoDbClient {

    /**
     * creat
     */
    void save(String collection, Map<String, Object> item);

    void save(String collection, List<Map<String, Object>> items);

    /**
     * read
     */
    Document findDocByItem(String collection, Map<String, Object> item);
    List<Document> findAllDocs(String collection);

    /**
     * update
     */

    void update(String collection, Map<String, Object> item);
    /**
     * delete
     */
}
