package cn.guanxiaoda.spider.models;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Data
@Builder
public class Task {

    private String id;
    private String name;
    private String stage;
    private Map<String, Object> ctx;
}
