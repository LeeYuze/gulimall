package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-23 16:56:11
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils unreceiveList(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> purchaseIds);

    void donePurchase(PurchaseDoneVo purchaseDoneVo);
}

