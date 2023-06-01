package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/28
 */
@Data
public class Catelog2Vo {

    private Long catalog1Id;
    private List<Catalog3Vo> catalog3List;
    private Long id;
    private String name;

    @Data
    public static class Catalog3Vo {
        private Long catalog2Id;//父分类 2级分类id
        private Long id;
        private String name;
    }
}
