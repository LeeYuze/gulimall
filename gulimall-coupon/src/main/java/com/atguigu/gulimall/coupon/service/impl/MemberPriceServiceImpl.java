package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.MemberPriceDao;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;


@Service("memberPriceService")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceDao, MemberPriceEntity> implements MemberPriceService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberPriceEntity> page = this.page(
                new Query<MemberPriceEntity>().getPage(params),
                new QueryWrapper<MemberPriceEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(SkuReductionTo skuReductionTo) {
        List<MemberPriceEntity> memberPriceEntities = skuReductionTo.getMemberPrice().stream().map(p -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberPrice(p.getPrice());
            memberPriceEntity.setMemberLevelId(p.getId());
            memberPriceEntity.setMemberLevelName(p.getName());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(p-> p.getMemberPrice().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

        if (!memberPriceEntities.isEmpty()) {
            this.saveBatch(memberPriceEntities);
        }
    }

}