package com.atguigu.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Resource
    private ElasticsearchClient esClient;

    @Test
    void contextLoads() {

        System.out.println(esClient);
    }

}
