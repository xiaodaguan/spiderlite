package cn.guanxiaoda.spider.http;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UnirestTest {
    @Autowired ClientPool clientPool;

    @Test
    public void proxyTest() throws UnirestException {
        Unirest.setHttpClient(clientPool.getApacheClient());
        System.out.println(Unirest.get("http://ip.chinaz.com/getip.aspx").asString().getBody());
    }
}
