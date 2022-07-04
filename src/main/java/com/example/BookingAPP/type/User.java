package com.example.BookingAPP.type;

import com.example.BookingAPP.entity.UserEntity;
import lombok.Data;
import org.apache.catalina.LifecycleState;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private Integer id;
    private String email;
    private String password;
    private List<Event> createdEvents = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public static User fromEntity(UserEntity userEntity) {
        User user = new User();
        user.setEmail(userEntity.getEmail());
        user.setId(userEntity.getId());
        return user;
    }


}
