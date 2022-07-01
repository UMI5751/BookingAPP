package com.example.BookingAPP.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.entity.UserEntity;
import com.example.BookingAPP.mapper.EventEntityMapper;
import com.example.BookingAPP.mapper.UserEntityMapper;
import com.example.BookingAPP.type.Event;
import com.example.BookingAPP.type.User;
import com.example.BookingAPP.type.UserInput;
import com.netflix.graphql.dgs.*;
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
    public List<User> users() {
        //select all userEntity
        List<UserEntity> userEntityList = userEntityMapper.selectList(null);
        List<User> userList = userEntityList.stream()
                .map(User::fromEntity).toList();
        return userList;
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


}