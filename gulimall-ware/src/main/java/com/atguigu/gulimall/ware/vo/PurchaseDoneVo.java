package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/23
 */
@Data
public class PurchaseDoneVo {
    private Long id;

    private List<PurchaseDoneItemVo> items;
}
