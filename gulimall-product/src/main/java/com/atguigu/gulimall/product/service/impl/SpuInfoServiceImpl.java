package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.Bounds;
import com.atguigu.gulimall.product.vo.Images;
import com.atguigu.gulimall.product.vo.Skus;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * @author lihaohui
 */
@Service("spuInfoService")
@RequiredArgsConstructor
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService spuInfoDescService;

    private final SpuImagesService spuImagesService;

    private final ProductAttrValueService productAttrValueService;

    private final SkuInfoService skuInfoService;

    private final SkuImagesService skuImagesService;

    private final SkuSaleAttrValueService skuSaleAttrValueService;

    private final CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(q->{
                q.eq("id", key).or().like("spu_name", key);
            });
        }

        String catelogId = (String)params.get("catelogId");
        if (StringUtils.hasLength(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String)params.get("brandId");
        if (StringUtils.hasLength(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String status = (String)params.get("status");
        if (StringUtils.hasLength(status)) {
            queryWrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    private void saveSpuBounds(Long spuId, Bounds bounds) {
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }
    }

    private void saveSkuReductionTo(Long skuId, Skus sku) {
        SkuReductionTo skuReductionTo = new SkuReductionTo();
        BeanUtils.copyProperties(sku, skuReductionTo);
        skuReductionTo.setSkuId(skuId);
        R r = couponFeignService.saveBySkuReductionTo(skuReductionTo);
        if (r.getCode() != 0) {
            log.error("远程保存sku优惠信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SpuSaveVo spuSaveVo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        this.save(spuInfoEntity);

        Long spuId = spuInfoEntity.getId();

        //2、保存Spu的描述图片 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", spuSaveVo.getDecript()));
        spuInfoDescService.save(spuInfoDescEntity);

        //3、保存spu的图片集 pms_spu_images
        spuImagesService.saveBySpuId(spuId, spuSaveVo.getImages());

        //4、保存spu的规格参数;pms_product_attr_value
        productAttrValueService.saveBySpuId(spuId, spuSaveVo.getBaseAttrs());

        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        saveSpuBounds(spuId, spuSaveVo.getBounds());

        //6、保存当前spu对应的所有sku信息；
        List<Skus> skus = spuSaveVo.getSkus();
        if (!skus.isEmpty()) {
            for (Skus sku : skus) {
                //6.1）、sku的基本信息；pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
                skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
                skuInfoEntity.setSkuDefaultImg(sku.getImages().stream().filter(img->img.getDefaultImg() == 1).findFirst().map(Images::getImgUrl).orElse(""));
                skuInfoService.saveBySpuId(spuId, skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                //6.2）、sku的图片信息；pms_sku_image
                skuImagesService.saveBySkuId(skuId, sku.getImages());

                //6.3）、sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBySkuId(skuId, sku.getAttr());

                //6.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                saveSkuReductionTo(skuId, sku);
            }
        }
    }

}