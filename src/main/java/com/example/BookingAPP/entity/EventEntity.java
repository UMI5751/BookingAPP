package com.example.BookingAPP.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.BookingAPP.type.EventInput;
import com.example.BookingAPP.util.DateUtil;
import lombok.Data;

import java.util.Date;

@Data
@TableName (value = "event")
public class EventEntity {
    //indicate this is the table ID and is serial
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String description;
    private float price;
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
