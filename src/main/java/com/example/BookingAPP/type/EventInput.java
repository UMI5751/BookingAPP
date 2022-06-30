package com.example.BookingAPP.type;

import lombok.Data;

@Data
public class EventInput {
    private String title;
    private String description;
    private float price;
    private String date;
}
