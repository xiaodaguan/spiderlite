package cn.guanxiaoda.spider.http;

import com.google.common.collect.ImmutableMap;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.Proxy;
import java.util.stream.IntStream;

/**
 * @author guanxiaoda
 * @date 2018/5/15
 */
//@SpringBootTest
//@RunWith(SpringRunner.class)
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
                    } finally {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    @Test
    public void clientParamTest() {
        OkHttpClient client = clientPool.getOkClient();
        Proxy proxy = client.proxy();

        System.out.println(proxy.address().toString());

    }

    @Test
    public void doubanTest() {
//        GET
        try {
            System.out.println(
                    new OkHttpClient.Builder()
                            .build()
                            .newCall(
                                    new Request.Builder()
                                            .get()
                                            .url("https://m.douban.com/rexxar/api/v2/movie/27133303/interests?count=25&order_by=hot&start=400&ck=&for_mobile=1")
//                                            .url("https://m.douban.com/rexxar/api/v2/movie/27133303/interests?count=25&order_by=hot&start=950&ck=&for_mobile=1")
                                            .headers(Headers.of(ImmutableMap.<String, String>builder()
                                                    .put("Accept", "application/json")
                                                    .put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
                                                    .put("Connection", "keep-alive")
                                                    .put("Host", "m.douban.com")
                                                    .put("Referer", "https://m.douban.com/movie/subject/27133303/comments?sort=time")
                                                    .put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3443.0 Mobile Safari/537.36")
                                                    .put("X-Requested-With", "XMLHttpRequest")
                                                    .build()))
                                            .build()
                            ).execute().body().string()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
