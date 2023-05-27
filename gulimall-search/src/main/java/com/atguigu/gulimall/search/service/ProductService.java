package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/27
 */
public interface ProductService {
    boolean up(List<SkuEsModel> skuEsModelList);
}
