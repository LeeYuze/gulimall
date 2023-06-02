package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/6/1
 */
@Data
public class SearchResult {
    private List<SkuEsModel> products;

    /**
     * 以下是分页信息
     */
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<Integer> pageNavs;

    private List<BrandVo> brands;
    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    private List<CatalogVo> catalogs;
    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    private List<AttrVo> attrs;
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    //面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    private List<Long> attrIds = new ArrayList<>();
}
