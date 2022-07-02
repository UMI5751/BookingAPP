package com.example.BookingAPP.custom;

import com.example.BookingAPP.entity.UserEntity;
import com.example.BookingAPP.mapper.UserEntityMapper;
import com.example.BookingAPP.util.TokenUtil;
import com.netflix.graphql.dgs.context.DgsCustomContextBuilderWithRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

//to get http header
//dgs will build auth context with @Component automatically, register spring bean into IOC
@Component
//for log.info()
@Slf4j
public class AuthContextBuilder implements DgsCustomContextBuilderWithRequest {

    //field name in http headers
    static final String AUTHORIZATION_HEADER = "Authorization";

    //userEntityMapper, is for querying data with DB
    private final UserEntityMapper userEntityMapper;

    public AuthContextBuilder(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    //use build method to acquire and parse HTTP header and build context
    @Override
    public Object build(@Nullable Map map, @Nullable HttpHeaders httpHeaders, @Nullable WebRequest webRequest) {
        log.info("building Auth Context...");
        AuthContext authContext = new AuthContext();

        //if http header does not contain authorization field, return empty authContext object
        if (!httpHeaders.containsKey(AUTHORIZATION_HEADER)) {
            log.info("unauthenticated user");
            return authContext;
        }

        //get first field "Authorization"
        String authorization = httpHeaders.getFirst(AUTHORIZATION_HEADER);
        //there is "bearer" in the front of token
        String token = authorization.replace("Bearer", "");

        //get user get by parse token
        Integer userId;
        try {
            userId = TokenUtil.verifyToken(token);
        } catch (Exception e) {
            authContext.setTokenInvalid(true);
            return authContext;
        }

        //use mybatis to query
        UserEntity userEntity = userEntityMapper.selectById(userId);
        //in case token secretkey leaked
        if (userEntity == null) {
            authContext.setTokenInvalid(true);
            return authContext;
        }

        authContext.setUserEntity(userEntity);
        log.info("authenticate successfully, userId = {}", userId);

        return authContext;
    }
}
