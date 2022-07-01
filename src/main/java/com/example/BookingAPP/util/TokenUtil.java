package com.example.BookingAPP.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Base64;
import java.util.Date;

public class TokenUtil {

    static final String issuer = "Admin";
    static final String USER_ID = "userID";

    static final long MILLI_SECONDS_IN_HOUR  = 60 * 60 * 1 * 1000;
    static Algorithm algorithm = Algorithm.HMAC256("secretkey");

    //create signin token for user
    //expirationInHour: how many hours the token will expire
    public static String signToken(Integer userId, int expirationInHour) {
        //
        String token = JWT.create()
                .withIssuer(issuer)
                //combine with userId
                .withClaim(USER_ID, userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + MILLI_SECONDS_IN_HOUR * expirationInHour))
                //hash algo
                .sign(algorithm);

        return token;

    }

    //verifying method
    public static Integer verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);
        //get userID
        Integer userId = jwt.getClaim(USER_ID).asInt();
        return userId;
    }

}
