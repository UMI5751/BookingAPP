package com.example.BookingAPP.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.BookingAPP.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

//indicate this is a spring bean
@Mapper
public interface UserEntityMapper extends BaseMapper<UserEntity> {


}

