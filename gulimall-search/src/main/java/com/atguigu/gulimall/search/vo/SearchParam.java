package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/6/1
 */
@Data
public class SearchParam {

    private String keyword;

    private Long catalog3Id;

    /**
     * 排序条件
     *  sort=saleCount_asc/desc 倒序
     *  sort=skuPrice_asc/desc 根据价格
     *  sort=hotScore_asc/desc
     */
    private String sort;

    /**
     * hasStock(是否有货) skuPrice区间 brandId catalog3Id attrs
     * hasStock 0/1
     * skuPrice=1_500 500_ _500
     * brandId = 1
     * attrs1_5寸_6寸
     * // 0 无库存 1有库存
     */
    private Integer hasStock;

    /**
     * 价格区查询
     */
    private String skuPrice;

    /**
     * 多个品牌id
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生所有的查询条件
     */
    private String _queryString;
}
