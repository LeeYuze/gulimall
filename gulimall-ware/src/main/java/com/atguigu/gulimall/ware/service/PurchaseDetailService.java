package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-23 16:56:11
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void mergePurchase(Long finalPurchaseId, List<Long> items);

    void receivedByPurchaseId(List<Long> purchaseIds);
}

