package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    @Null(message = "品牌id必须为空", groups = {AddGroup.class})
    @NotNull(message = "品牌id不能为空", groups = {UpdateGroup.class})
    private Long brandId;

    /**
     * 品牌名
     */
    @NotEmpty(message = "name品牌名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 品牌logo地址
     */
    @URL(message = "logo地址必须为合理的url地址", groups = {AddGroup.class, UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    @NotEmpty(message = "descript介绍不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ListValue(vals = {0, 1}, groups = {AddGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @Pattern(regexp = "^[a-zA-z]$", message = "firstLetter检索首字母需要是一个字母",groups = {AddGroup.class,UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @Min(value = 0, message = "sort排序需要大于等于0！", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;

}
