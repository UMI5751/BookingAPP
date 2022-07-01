package com.example.BookingAPP.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.BookingAPP.type.EventInput;
import com.example.BookingAPP.type.UserInput;
import com.example.BookingAPP.util.DateUtil;
import lombok.Data;

@Data
@TableName(value = "tb_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String email;
    private String password;

    public static UserEntity fromUserInput(UserInput userInput) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userInput.getEmail());
        userEntity.setPassword(userInput.getPassword());
        return userEntity;
    }
}
