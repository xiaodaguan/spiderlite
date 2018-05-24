package cn.guanxiaoda.spider.conf;

import com.alibaba.fastjson.serializer.PropertyFilter;

/**
 * @author guanxiaoda
 * @date 2018/5/22
 */
public class FastJsonConf {
    public static PropertyFilter filter = (o, s, o1) -> !"fetched".equalsIgnoreCase(s) && !"parsed".equalsIgnoreCase(s);
}
