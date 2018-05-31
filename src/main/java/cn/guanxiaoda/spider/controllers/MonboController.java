package cn.guanxiaoda.spider.controllers;

import cn.guanxiaoda.spider.dao.mongodb.IMongoDbClient;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guanxiaoda
 * @date 2018/5/31
 */
@RestController
@RequestMapping("/mongo")
public class MonboController {

    @Autowired IMongoDbClient mongoDbClient;


    @GetMapping("/getdocslike/{coll}/{field}/{val}")
    public String getDocsByFieldLike(@PathVariable("coll") String collection, @PathVariable("field") String field, @PathVariable("val") Object value) {
        return JSON.toJSONString(mongoDbClient.findDocByFieldLike(collection, field, value));
    }


    @GetMapping("/findcollnames")
    public String getAllCollNames() {
        return JSON.toJSONString(mongoDbClient.findAllCollectionNames());
    }
}
