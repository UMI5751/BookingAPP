package com.example.BookingAPP.type;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
//@Accessors allow to use builder to set data, new Authdata().setUserId().setToken().setXXX()...
@Accessors(chain = true)
public class AuthData {
    private Integer userId;
    private String token;
    private Integer tokenExpiration;
}
