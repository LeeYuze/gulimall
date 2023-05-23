package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/5/23
 */
@Data
public class PurchaseDoneItemVo {

    //itemId:1,status:4,reason:""
    private Long itemId;

    private Integer status;

    private String reason;
}
