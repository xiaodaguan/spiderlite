package cn.guanxiaoda.spider.http;

import okhttp3.HttpUrl;
import org.junit.Test;

import java.net.MalformedURLException;

/**
 * @author guanxiaoda
 * @date 2018/5/19
 */
public class UrlTest {


    @Test
    public void urlParseTest() throws MalformedURLException {
        String url = "https://www.google.ca/search?q=java+%E8%A7%A3%E6%9E%90%E5%9F%9F%E5%90%8D&oq=java+%E8%A7%A3%E6%9E%90%E5%9F%9F%E5%90%8D&aqs=chrome..69i57j69i65j69i60l2j69i61j69i65.2715j0j1&sourceid=chrome&ie=UTF-8/";
        System.out.println(HttpUrl.parse(url).topPrivateDomain());

        url = "http://www.baidu.com";
        System.out.println(HttpUrl.parse(url).host());

        url = "http://baidu.com";
        System.out.println(HttpUrl.parse(url).host());


    }


}
