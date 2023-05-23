/**
  * Copyright 2023 json.cn 
  */
package com.atguigu.gulimall.product.vo;
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
public class SpuSaveVo {
    private Long catalogId;

    private Long brandId;

    private String spuName;

    private String spuDescription;

    private BigDecimal weight;

    private Integer publishStatus;

    private Bounds bounds;

    private List<String> decript;

    private List<String> images;

    private List<BaseAttrs> baseAttrs;

    private List<Skus> skus;
}