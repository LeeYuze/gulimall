package com.atguigu.common.to.es;

import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/5/27
 */
@Data
public class SkuHasStockTo {
    private Long skuId;
    private Boolean hasStock;
}
