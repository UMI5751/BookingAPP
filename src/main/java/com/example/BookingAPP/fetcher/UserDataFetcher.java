package com.example.BookingAPP.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.BookingAPP.custom.AuthContext;
import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.entity.UserEntity;
import com.example.BookingAPP.mapper.EventEntityMapper;
import com.example.BookingAPP.mapper.UserEntityMapper;
import com.example.BookingAPP.type.*;
import com.example.BookingAPP.util.TokenUtil;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

//Simple Logging Facade for Java
@Slf4j
@DgsComponent
public class UserDataFetcher {
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventEntityMapper eventEntityMapper;

    public UserDataFetcher(UserEntityMapper userEntityMapper, PasswordEncoder passwordEncoder, EventEntityMapper eventEntityMapper) {
        this.userEntityMapper = userEntityMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventEntityMapper = eventEntityMapper;
    }

    //solver for select all Users in DB
    @DgsQuery
    public List<User> users(DataFetchingEnvironment dfe) {
        //only authenticated user is allowed to query all users in DB
        //Dgs will help to acquire authContext built by AuthContextBuilder
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        //if not authenticated, it will throw exception
        authContext.ensureAuthenticated();

        //select all userEntity
        List<UserEntity> userEntityList = userEntityMapper.selectList(null);
        List<User> userList = userEntityList.stream()
                .map(User::fromEntity).toList();
        return userList;
    }

    //extend UserDataFetcher class to implement login function.
    @DgsQuery
    public AuthData login(@InputArgument LoginInput loginInput) {
        UserEntity userEntity = this.findUserByEmail(loginInput.getEmail());
        if (userEntity == null) {
            throw new RuntimeException("This email has not been registered");
        }
        boolean match = passwordEncoder.matches(loginInput.getPassword(), userEntity.getPassword());
        if (!match) {
            throw new RuntimeException("Incorrect password!");
        }

        String token = TokenUtil.signToken(userEntity.getId(), 1);

        AuthData authData = new AuthData()
                .setUserId(userEntity.getId())
                .setToken(token)
                .setTokenExpiration(1);

        return authData;

    }


    @DgsMutation
    public User createUser(@InputArgument UserInput userInput) {
        ensureUserNotExists(userInput);

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setEmail(userInput.getEmail());
        //hash password by encoding
        newUserEntity.setPassword(passwordEncoder.encode(userInput.getPassword()));

        userEntityMapper.insert(newUserEntity);

        User newUser = User.fromEntity(newUserEntity);
        //before pass newUser, must set password as null for security reason
        newUser.setPassword(null);
        return newUser;
    }

    //inject List<Event> into User.createdEvents, implemented by DGS's resolver
    //in annotation, Match variable names in User.java
    @DgsData(parentType = "User", field = "createdEvents")
    public List<Event> createEvents(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        QueryWrapper<EventEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EventEntity::getCreatorID, user.getId());
        List<EventEntity> eventEntityList = eventEntityMapper.selectList(queryWrapper);
        List<Event> eventList = eventEntityList.stream().map(Event::fromEntity).toList();
        return eventList;
    }


    private void ensureUserNotExists(UserInput userInput) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        // Stream.of().(A::func) equals forEach(x -> A.func(x))
        queryWrapper.lambda().eq(UserEntity::getEmail, userInput.getEmail());
        if (userEntityMapper.selectCount(queryWrapper) >= 1) {
            throw new RuntimeException("Email already registered");
        }
    }

    //find the user by email
    private UserEntity findUserByEmail(String email) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getEmail, email);
        //if found, return one
        return userEntityMapper.selectOne(queryWrapper);
    }


}
