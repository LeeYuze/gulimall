package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        String key = (String)params.get("key");
        if (StringUtils.hasLength(key)) {
            wrapper.and((obj)->{
                obj.eq("sku_id", key).or().eq("purchase_id", key);
            });
        }

        String status = (String)params.get("status");
        if (StringUtils.hasLength(status)) {
            wrapper.eq("status", status);
        }

        String wareId = (String)params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params), wrapper
        );


        return new PageUtils(page);
    }

    @Override
    public void mergePurchase(Long finalPurchaseId, List<Long> items) {
        if(!items.isEmpty()) {
            List<PurchaseDetailEntity> purchaseDetailEntityList = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setStatus(WareConstant.purchaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            this.updateBatchById(purchaseDetailEntityList);
        }

    }

    @Override
    public void receivedByPurchaseId(List<Long> purchaseIds) {
        for (Long purchaseId : purchaseIds) {
            List<PurchaseDetailEntity> purchaseDetailEntityList = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId));
            List<PurchaseDetailEntity> updateList = purchaseDetailEntityList
                    .stream()
                    .peek(purchaseDetailEntity -> purchaseDetailEntity.setStatus(WareConstant.purchaseDetailStatusEnum.BUYING.getCode()))
                    .collect(Collectors.toList());
            this.updateBatchById(updateList);
        }
    }

}