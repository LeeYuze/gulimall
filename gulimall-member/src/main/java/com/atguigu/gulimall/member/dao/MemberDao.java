package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lihaohui
 * @email qw110011qw@gmail.com
 * @date 2023-05-18 21:48:37
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
