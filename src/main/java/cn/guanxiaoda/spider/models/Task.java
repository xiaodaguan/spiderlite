package cn.guanxiaoda.spider.models;

import lombok.Data;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Data
public class Task {

    private String name;
    private String url;
    private Object fetched;
    private Object parsed;
    private Object item;


    public Task setName(String name) {
        this.name = name;
        return this;
    }

    public Task setUrl(String url) {
        this.url = url;
        return this;
    }

    public Task setFetched(Object fetched) {
        this.fetched = fetched;
        return this;
    }

    public Task setParsed(Object parsed) {
        this.parsed = parsed;
        return this;
    }

    public Task setItem(Object item) {
        this.item = item;
        return this;
    }
}
