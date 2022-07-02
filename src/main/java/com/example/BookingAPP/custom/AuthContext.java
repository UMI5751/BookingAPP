package com.example.BookingAPP.custom;

import com.example.BookingAPP.entity.UserEntity;
import lombok.Data;

@Data
public class AuthContext {
    //store user information in ram, and remember this user has been authed
    private UserEntity userEntity;
    private boolean tokenInvalid;

    //ensure the user has been authenticated before some action
    public void ensureAuthenticated() {
        if (tokenInvalid) throw new RuntimeException("Invalid Token");
        if (userEntity == null) throw new RuntimeException("Not sign in, please sign in");
    }


}
