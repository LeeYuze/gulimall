package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /***
     * 查询分类，以树形式展示
     * @return 分类树
     */
    List<CategoryEntity> listWithTree();

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 查找catelog完整的路径id
     * @param categoryId 分类id
     * @return 分类id路径链
     */
    Long[] findPath(Long categoryId);

    /**
     * 修改三级分类
     * @param category 三级分类DTO
     */
    void updateDetail(CategoryEntity category);

    List<CategoryEntity> listOfLevel1();

    Map<Long, List<Catelog2Vo>> getCatalogJson();
}

