package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存品牌分类关联
     * @param categoryBrandRelation 品牌分类关联DTO
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 修改品牌名称通过品牌id
     * @param brandId 品牌id
     * @param name 品牌名称
     */
    void updateBrandNameById(Long brandId, String name);

    /**
     * 修改分类名称通过分类id
     * @param catId 分类id
     * @param name 分类名称
     */
    void updateCategoryNameById(Long catId, String name);
}

