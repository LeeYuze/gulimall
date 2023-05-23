package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBySpuId(Long spuId, List<BaseAttrs> baseAttrs);

    List<ProductAttrValueEntity> listBySpuId(Long spuId);

    void updateBatchBySpuAttr(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList);
}

