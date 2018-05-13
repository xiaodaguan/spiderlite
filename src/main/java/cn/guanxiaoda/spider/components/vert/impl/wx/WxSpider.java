package cn.guanxiaoda.spider.components.vert.impl.wx;

import cn.guanxiaoda.spider.components.vert.BaseSpider;
import cn.guanxiaoda.spider.components.vert.IProcessor;
import cn.guanxiaoda.spider.models.Task;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @author guanxiaoda
 * @date 2018/4/13
 */
@Component(value = "wxSpider")
public class WxSpider extends BaseSpider {

    @Autowired @Qualifier("wxStarter") IProcessor<Task> starter;
    @Autowired @Qualifier("wxFetcher") IProcessor<Task> fetcher;
    @Autowired @Qualifier("wxParser") IProcessor<Task> parser;
    @Autowired @Qualifier("wxPersister") IProcessor<Task> persister;
    @Autowired @Qualifier("wxPager") IProcessor<Task> pager;

    public void start() {

        setStarter(starter);
        addProcessor(starter, fetcher);
        addProcessor(fetcher, parser);
        addProcessor(parser, persister, pager);
        addProcessor(pager, fetcher);
        setTerminate(persister);

        Stream.of(
                "MzAxOTc0NzExNg==",
                "MzA3ODQ0Mzg2OA==",
                "MzUzMTA2NTU2Ng==",
                "MzIxMjE5MTE1Nw==",
                "MzIzNjI1ODc2OA==",
                "MzUxMzcxMzE5Ng==",
                "MjM5ODYxMDA5OQ==",
                "MzI4NDY5Mjc1Mg==",
                "MzAxODcyNjEzNQ==",
                "MzIzOTU0NTQ0MA=="
        ).map(fakeId ->
                Task.builder().ctx(
                        Maps.newHashMap(ImmutableMap.<String, Object>builder()
                                .put("fakeId", fakeId)
                                .put("token", "705391348")
                                .put("cookies", "noticeLoginFlag=1; ua_id=sTPaxE9j3XNEt5jlAAAAAKYAXptzCThn85jJJQN43Uk=; pgv_pvi=4797978624; mm_lang=zh_CN; noticeLoginFlag=1; remember_acct=xiaodaguan%40gmail.com; pgv_si=s691451904; uuid=4a8ddf4783ef6ff57aca9be896c0738b; ticket=a48b59cc0d21439b571d278ac790ccea37ad7ccf; ticket_id=gh_eecd327a369c; cert=usdJNGJ8kJGSnUNjmgcTNCIKT6G0n5Dg; data_bizuin=3595588900; bizuin=3550833854; data_ticket=3Y6paC7arOiFH2Q9KCzftcIcIv+NB+XmdtsQY53wsD90eo+5Ht9bmEQH2n40uN7B; slave_sid=RFpraXA4RUJRYndkRXhhRV9MOEdGSDVCSDZlM2pHMEFqWlNpYlkwYzhITDgyODJyWGZ2NzJHZ091a1NUZFp4WGtQdzZRVUZURktiRllTamd3MUtjeFA4R3dldjAxSUFWRDV4dlhNcEdGdnVGOHFmYkwzbVVGYU9IcXBBUnBDcjBQNEt2VUxHcG95a01GNFdZ; slave_user=gh_eecd327a369c; xid=f464753252d60696cda3b566928a6dbc; openid2ticket_oJFVQ1kBxXq5xBn4vhhUaIedXTao=32KT7nM+SpwX/lKvThrFFB5u8R+I5w31N/G6G0eLpJM=/W1Bm/0m68mEpcJcrnpMPQ4lRNQ4LHPqVlO0SSE8gYXPHvnK14jXAF; slave_sid=YlE3RmNrWWtrSVlieGlaUmE1eVV5QUR6ZWJUQ3dzZXlweFFVdWlFeVpwWFJUcjZDdFBhWXVWYW5LY0xmMURjSGhISjlJb2dHZkxiU2NMU1BKOUNhNnhWQVBTWFJhZHdfSVFvMGg2VUQ0d3NnczNTOUxNUzU0aTVJMm1yVVlJSjloSE84UWRUbEhwV1RxSHFl; slave_user=gh_eecd327a369c; xid=20c5b783bb13c38f65d2d0dbd8fee831; openid2ticket_oJFVQ1kBxXq5xBn4vhhUaIedXTao=0sf2EhALnQHDWpuWU6wC64qTzh1Zfb+FGyRCJvr7caU=")
                                .build())
                ).name("wx_papers").build()
        ).forEach(this::launch);
    }

}
