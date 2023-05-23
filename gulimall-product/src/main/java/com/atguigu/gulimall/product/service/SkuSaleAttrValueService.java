package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.Attr;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBySkuId(Long skuId, List<Attr> attr);
}

