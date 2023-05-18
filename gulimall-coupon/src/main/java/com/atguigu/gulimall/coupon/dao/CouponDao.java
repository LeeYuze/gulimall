package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 21:42:44
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
