package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/5/21
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 三级分类名称
     */
    private String catelogName;

    /**
     * 属性分组名称
     */
    private String groupName;


    /**
     * 三级分类路径
     */
    private Long[] catelogPath;
}
