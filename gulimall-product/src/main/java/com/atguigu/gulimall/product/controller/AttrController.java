package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 商品属性
 *
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 21:34:00
 */
@RestController
@RequestMapping("product/attr")
@RequiredArgsConstructor
public class AttrController {
    private final AttrService attrService;

    private final ProductAttrValueService productAttrValueService;

    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> productAttrValueEntityList) {
        productAttrValueService.updateBatchBySpuAttr(spuId, productAttrValueEntityList);
        return R.ok();
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseListForSpu(@PathVariable Long spuId) {

        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.listBySpuId(spuId);

        return R.ok().put("data", productAttrValueEntityList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/{attrType}/list/{categoryId}")
    public R listBaseAttr(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId, @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryPageByCategoryIdAndType(params, categoryId, attrType);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo attr = attrService.getAttrRespVoById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo) {
        attrService.updateById(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
