package com.example.BookingAPP.type;

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
}
