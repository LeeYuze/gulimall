package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneItemVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author lihaohui
 */
@Service("purchaseService")
@RequiredArgsConstructor
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    private final PurchaseDetailService purchaseDetailService;

    private final WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", WareConstant.purchaseStatusEnum.CREATED.getCode()).or().eq("status", WareConstant.purchaseStatusEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }


    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (Objects.isNull(purchaseId)) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.purchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        Long finalPurchaseId = purchaseId;

        purchaseDetailService.mergePurchase(finalPurchaseId, mergeVo.getItems());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void received(List<Long> purchaseIds) {
        if (purchaseIds.isEmpty()) {
            return;
        }

        List<PurchaseEntity> purchaseEntityList = purchaseIds
                .stream()
                .map(this::getById)
                .filter(purchaseEntity -> purchaseEntity.getStatus() == WareConstant.purchaseStatusEnum.CREATED.getCode() || purchaseEntity.getStatus() == WareConstant.purchaseStatusEnum.ASSIGNED.getCode())
                .peek(purchaseEntity -> purchaseEntity.setStatus(WareConstant.purchaseStatusEnum.RECEIVE.getCode()))
                .collect(Collectors.toList());

        this.updateBatchById(purchaseEntityList);

        purchaseDetailService.receivedByPurchaseId(purchaseIds);
    }

    private void updateStatus(Long id, Integer status) {
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(id);
        purchase.setStatus(status);
        this.updateById(purchase);
    }

    @Override
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {
        Long purchaseId = purchaseDoneVo.getId();

        List<PurchaseDetailEntity> updateList = new ArrayList<>();
        boolean purchaseFlag = true;
        // 修改采购单项状态
        for (PurchaseDoneItemVo item : purchaseDoneVo.getItems()) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item.getItemId());
            if (item.getStatus() == WareConstant.purchaseDetailStatusEnum.HASERROR.getCode()) {
                purchaseDetailEntity.setStatus(item.getStatus());
                purchaseFlag = false;
            } else {
                purchaseDetailEntity.setStatus(WareConstant.purchaseDetailStatusEnum.FINISH.getCode());
                // 采购成功->新增仓库库存
                PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(purchaseDetail.getSkuId(), purchaseDetail.getWareId(), purchaseDetail.getSkuNum());
            }
            updateList.add(purchaseDetailEntity);
        }

        if (!updateList.isEmpty()) {
            purchaseDetailService.updateBatchById(updateList);
        }

        // 修改采购单状态
        updateStatus(purchaseId, purchaseFlag ? WareConstant.purchaseStatusEnum.FINISH.getCode() : WareConstant.purchaseStatusEnum.HASERROR.getCode());
    }

}