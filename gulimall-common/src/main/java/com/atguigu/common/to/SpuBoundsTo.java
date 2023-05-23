package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lihaohui
 * @date 2023/5/23
 */
@Data
public class SpuBoundsTo {

    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
}
