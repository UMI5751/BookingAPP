package com.example.BookingAPP.type;

import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.util.DateUtil;
import lombok.Data;

//This is DTO
//@Data: lombok will create set/get method for this class automatically
@Data
public class Event {
    private String id;
    private String title;
    private String description;
    private float price;
    private String date;

    //create static method: transfer EventEntity to Event(DTO)
    public static Event fromEntity(EventEntity eventEntity) {
        Event event = new Event();
        event.setId(eventEntity.getId().toString());
        event.setTitle(eventEntity.getTitle());
        event.setDescription(eventEntity.getDescription());
        event.setPrice(eventEntity.getPrice());
        event.setDate(DateUtil.formatDateInISOString(eventEntity.getDate()));
        return event;
    }
}
