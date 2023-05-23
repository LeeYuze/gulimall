package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.Images;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBySkuId(Long skuId, List<Images> images);
}

