package com.example.BookingAPP.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.BookingAPP.type.EventInput;
import com.example.BookingAPP.util.DateUtil;
import lombok.Data;

import java.util.Date;

@Data
//indicate the name of the table in postgresql DB
@TableName (value = "tb_event")
public class EventEntity {
    //indicate this is the table ID and is serial
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String description;
    private float price;
    //Date type can be stored in postgresql
    private Date date;

    //convert DTO to Data Entity(for DB)
    public static EventEntity fromEventInput(EventInput input) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setTitle(input.getTitle());
        eventEntity.setDescription(input.getDescription());
        eventEntity.setDescription(input.getDescription());
        eventEntity.setPrice(input.getPrice());
        eventEntity.setDate(DateUtil.convertISOStringToDate(input.getDate()));
        return eventEntity;
    }
}
