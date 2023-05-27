package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/27
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ElasticsearchClient esClient;

    @Override
    public boolean up(List<SkuEsModel> skuEsModelList){

        List<BulkOperation> list = new ArrayList<>();

        for (SkuEsModel skuEsModel : skuEsModelList) {
            list.add(new BulkOperation.Builder().create(builder -> builder.index(EsConstant.PRODUCT_INDEX).id(skuEsModel.getSkuId().toString()).document(skuEsModel)).build());
        }


        try {
            BulkResponse response = esClient.bulk(builder -> builder.operations(list));
            return !response.errors();
        } catch (IOException e) {
            return false;
        }
    }
}
