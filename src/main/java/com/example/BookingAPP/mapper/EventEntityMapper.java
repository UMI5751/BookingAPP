package com.example.BookingAPP.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.BookingAPP.entity.EventEntity;
import org.apache.ibatis.annotations.Mapper;

//this is an interface for Database postgresql
//mybatis will automatically finish by AOP
@Mapper
public interface EventEntityMapper extends BaseMapper<EventEntity> {
}
