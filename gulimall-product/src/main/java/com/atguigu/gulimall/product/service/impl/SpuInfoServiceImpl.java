package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.es.SkuHasStockTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.Bounds;
import com.atguigu.gulimall.product.vo.Images;
import com.atguigu.gulimall.product.vo.Skus;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import jdk.nashorn.internal.ir.CallNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * @author lihaohui
 */
@Service("spuInfoService")
@RequiredArgsConstructor
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService spuInfoDescService;

    private final SpuImagesService spuImagesService;

    private final ProductAttrValueService productAttrValueService;

    private final SkuInfoService skuInfoService;

    private final SkuImagesService skuImagesService;

    private final SkuSaleAttrValueService skuSaleAttrValueService;

    private final CouponFeignService couponFeignService;

    private final BrandService brandService;

    private final CategoryService categoryService;

    private final AttrService attrService;

    private final WareFeignService wareFeignService;

    private final SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(q -> {
                q.eq("id", key).or().like("spu_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.hasLength(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.hasLength(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
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
                skuInfoEntity.setSkuDefaultImg(sku.getImages().stream().filter(img -> img.getDefaultImg() == 1).findFirst().map(Images::getImgUrl).orElse(""));
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

    private void copyFieldsToSkuEsModelBySku(SkuInfoEntity skuInfo, SkuEsModel skuEsModel) {
        BeanUtils.copyProperties(skuInfo, skuEsModel);

        skuEsModel.setSkuPrice(skuInfo.getPrice());
        skuEsModel.setSkuImg(skuInfo.getSkuDefaultImg());
    }

    private void setOrderFieldsToSkuEsModelBySku(SkuInfoEntity skuInfo, SkuEsModel skuEsModel) {
        skuEsModel.setHotScore(0L);

        BrandEntity brandEntity = brandService.getById(skuInfo.getBrandId());
        skuEsModel.setBrandName(brandEntity.getName());
        skuEsModel.setBrandImg(brandEntity.getLogo());

        CategoryEntity categoryEntity = categoryService.getById(skuInfo.getCatalogId());
        skuEsModel.setCatalogName(categoryEntity.getName());
    }

    private void setAttrsToSkuEsModel(Long spuId, SkuEsModel skuEsModel) {
        List<AttrEntity> attrEntityList = attrService.listOfSearchAttrBySpuId(spuId);
        List<SkuEsModel.Attrs> attrs = attrEntityList.stream().map(attr -> {
            SkuEsModel.Attrs esAttr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attr, esAttr);
            return esAttr;
        }).collect(Collectors.toList());
        skuEsModel.setAttrs(attrs);
    }

    public void updateStatusById(Long spuId, int code) {
        SpuInfoEntity spuInfoEntity = getById(spuId);
        spuInfoEntity.setPublishStatus(code);
        updateById(spuInfoEntity);
    }

    @Override
    public void upById(Long spuId) {
        // 1、获取spu下的所有sku
        List<SkuInfoEntity> skus = skuInfoService.getBySpuId(spuId);
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        Map<Long, Boolean> hasStockMap = null;
        R hasStockR = wareFeignService.hasStock(skuIds);
        if (hasStockR.getCode() == 0) {
            List<SkuHasStockTo> list = hasStockR.getData(new TypeReference<List<SkuHasStockTo>>() {
            });
            hasStockMap = list.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }

        ArrayList<SkuEsModel> saveList = new ArrayList<>();

        for (SkuInfoEntity sku : skus) {
            SkuEsModel skuEsModel = new SkuEsModel();
            copyFieldsToSkuEsModelBySku(sku, skuEsModel);

            // 其他参数
            setOrderFieldsToSkuEsModelBySku(sku, skuEsModel);

            // 设置可搜索的属性
            setAttrsToSkuEsModel(spuId, skuEsModel);

            // 设置是否有库存
            if (Objects.isNull(hasStockMap)) {
                skuEsModel.setHasStock(false);
            } else {
                skuEsModel.setHasStock(hasStockMap.get(sku.getSkuId()));
            }

            saveList.add(skuEsModel);
        }

        // 保存到es
        R saveR = searchFeignService.productStatusUp(saveList);
        if (saveR.getCode() == 0) {
            updateStatusById(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }
    }

}