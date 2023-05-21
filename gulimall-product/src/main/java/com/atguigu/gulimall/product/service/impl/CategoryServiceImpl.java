package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> allCategoryList = baseMapper.selectList(null);

        List<CategoryEntity> treeRes = allCategoryList.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(0L))
                .map(category -> {
                    category.setChildren(getChildren(category, allCategoryList));
                    return category;
                }).sorted((menu1, menu2) -> menu1.getSort() == null ? 0 : menu1.getSort() - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());

        return treeRes;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //TODO 删除前检测，判断是否有引用

        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] findPath(Long categoryId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(categoryId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);

        if(!Objects.isNull(category.getName())) {
            categoryBrandRelationService.updateCategoryNameById(category.getCatId(), category.getName());
        }
    }

    private List<Long> findParentPath(Long categoryId, List<Long> paths) {
        paths.add(categoryId);

        CategoryEntity category = this.getById(categoryId);
        if(!category.getParentCid().equals(0L)) {
            findParentPath(category.getParentCid(), paths);
        }

        return paths;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(category -> category.getParentCid().equals(root.getCatId()))
                .map(category -> {
                    category.setChildren(getChildren(category, all));
                    return category;
                }).sorted((menu1, menu2) -> menu1.getSort() == null ? 0 : menu1.getSort() - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
    }

}