package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 18:24:18
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存属性
     * @param attr 属性vo
     */
    void saveAttr(AttrVo attr);

    PageUtils queryPageByCategoryIdAndType(Map<String, Object> params, Long categoryId, String attrType);

    /**
     * 获取属性参数
     * @param attrId 属性id
     * @return 属性vo
     */
    AttrRespVo getAttrRespVoById(Long attrId);

    /**
     * 修改属性
     * @param attrVo 属性vo
     */
    void updateById(AttrVo attrVo);

    /**
     * 查询已关联的属性，通过分组id
     * @param attrGroupId 分组id
     * @return 属性
     */
    List<AttrEntity> getAttrRelation(Long attrGroupId);

    /**
     * 查询该分组下未关联的属性
     * @param attrGroupId 分组id
     * @return 属性
     */
    PageUtils getAttrNoRelation(Map<String, Object> params, Long attrGroupId);
}

