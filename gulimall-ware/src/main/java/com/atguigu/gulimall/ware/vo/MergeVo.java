package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/23
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;
}
