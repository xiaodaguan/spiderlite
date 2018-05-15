package cn.guanxiaoda.spider.http;

import okhttp3.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OkTest {


    @Autowired ClientPool clientPool;

    @Test
    public void proxyTest() throws IOException {
        IntStream.range(0, 10).forEach(
                i -> {
                    try {
                        System.out.println(
                                clientPool.getOkClient()
                                        .newCall(new Request.Builder()
                                                .url("http://ip.chinaz.com/getip.aspx")
                                                .build())
                                        .execute()
                                        .body()
                                        .string()
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }
}
