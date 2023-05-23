/**
  * Copyright 2023 json.cn 
  */
package com.atguigu.gulimall.product.vo;
import com.atguigu.common.to.MemberPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2023-05-22 22:9:33
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Skus {


    private String skuName;

    private BigDecimal price;

    private String skuTitle;

    private String skuSubtitle;

    private Integer fullCount;

    private BigDecimal discount;

    private Integer countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private Integer priceStatus;

    private List<String> descar;

    private List<Attr> attr;

    private List<Images> images;

    private List<MemberPrice> memberPrice;

}