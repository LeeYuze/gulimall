package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.to.es.SkuHasStockTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
@RequiredArgsConstructor
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    private final ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        String skuId = (String) params.get("skuId");
        if (StringUtils.hasLength(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params), wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntityList = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntityList.isEmpty()) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            wareSkuEntity.setStock(skuNum);

            R info = productFeignService.skuInfo(skuId);
            Map<String, Object> skuInfo =  (Map<String, Object>) info.get("skuInfo");
            if (info.getCode() == 0) {
                wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
            }
            this.save(wareSkuEntity);
        } else {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockTo> hasStock(List<Long> skuIds) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds);
        List<WareSkuEntity> wareSkuEntityList = list(wrapper);

        return skuIds.stream().map(skuId->{
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            skuHasStockTo.setSkuId(skuId);

            Optional<WareSkuEntity> optionalWareSku = wareSkuEntityList.stream().filter(ware -> ware.getSkuId().equals(skuId)).findFirst();
            if (optionalWareSku.isPresent()) {
                WareSkuEntity wareSkuEntity = optionalWareSku.get();
                skuHasStockTo.setHasStock((wareSkuEntity.getStock() - wareSkuEntity.getStockLocked()) > 0);
            } else {
                skuHasStockTo.setHasStock(false);
            }

            return skuHasStockTo;
        }).collect(Collectors.toList());
    }

}