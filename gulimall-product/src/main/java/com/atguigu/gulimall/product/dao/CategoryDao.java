package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
