package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;

@Slf4j
@Service("categoryService")
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;

    private final RedisTemplate redisTemplate;

    private final RedissonClient redissonClient;

    private final String CATEGORY_JSON_CACHE_KEY = "category_json";

    private final String CATEGORY_JSON_LOCK_CACHE_KEY = "lock_category_json";

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
    @CacheEvict(value = {"category"}, allEntries = true)
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);

        if (!Objects.isNull(category.getName())) {
            categoryBrandRelationService.updateCategoryNameById(category.getCatId(), category.getName());
        }
    }

    @Override
    @Cacheable(value = {"category"}, key = "#root.methodName")
    public List<CategoryEntity> listOfLevel1() {

        return list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    private Map<Long, List<Catelog2Vo>> getCatalogJsonFromDB() {
        log.info("获取了数据库");
        List<CategoryEntity> list = list();
        List<CategoryEntity> rootList = filterByParentCid(list, 0L);

        return rootList.stream().collect(Collectors.toMap(CategoryEntity::getCatId, rootCategory -> {
            List<CategoryEntity> level2CategoryList = filterByParentCid(list, rootCategory.getCatId());
            return level2CategoryList.stream().map(c2 -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo();
                catelog2Vo.setId(c2.getCatId());
                catelog2Vo.setCatalog1Id(rootCategory.getCatId());
                catelog2Vo.setName(c2.getName());

                List<Catelog2Vo.Catalog3Vo> catalog3VoList = filterByParentCid(list, c2.getCatId()).stream().map(c3 -> {
                    Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo();
                    catalog3Vo.setCatalog2Id(c2.getCatId());
                    catalog3Vo.setId(c3.getCatId());
                    catalog3Vo.setName(c3.getName());
                    return catalog3Vo;
                }).collect(Collectors.toList());

                catelog2Vo.setCatalog3List(catalog3VoList);

                return catelog2Vo;
            }).collect(Collectors.toList());
        }));
    }

    @Override
    @Cacheable(value = {"category"}, key = "#root.methodName")
    public Map<Long, List<Catelog2Vo>> getCatalogJson() {
        return getCatalogJsonFromDB();
    }

    public Map<Long, List<Catelog2Vo>> getCatalogJson2() {
        Map<Long, List<Catelog2Vo>> map = (Map<Long, List<Catelog2Vo>>) redisTemplate.opsForValue().get(CATEGORY_JSON_CACHE_KEY);
        if (!Objects.isNull(map)) {
            return map;
        }
        RLock lock = redissonClient.getLock(CATEGORY_JSON_LOCK_CACHE_KEY);
        lock.lock();
        Map<Long, List<Catelog2Vo>> catalogJsonFromDB;
        try {
            catalogJsonFromDB = getCatalogJsonFromDB();
            redisTemplate.opsForValue().set(CATEGORY_JSON_CACHE_KEY, catalogJsonFromDB, 3600L, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
        return catalogJsonFromDB;
    }

    public List<CategoryEntity> filterByParentCid(List<CategoryEntity> list, Long parentCid) {
        return list.stream().filter(c -> c.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }

    private List<Long> findParentPath(Long categoryId, List<Long> paths) {
        paths.add(categoryId);

        CategoryEntity category = this.getById(categoryId);
        if (!category.getParentCid().equals(0L)) {
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