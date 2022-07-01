package com.example.BookingAPP.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.entity.UserEntity;
import com.example.BookingAPP.mapper.EventEntityMapper;
import com.example.BookingAPP.mapper.UserEntityMapper;
import com.example.BookingAPP.type.Event;
import com.example.BookingAPP.type.EventInput;
import com.example.BookingAPP.type.User;
import com.netflix.graphql.dgs.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//implement query in schema.graphqls, this is a solver(fetcher)
@DgsComponent
public class EventDataFetcher {
    //no need to store in memory
    //private List<Event> events = new ArrayList<>();

    //store in DB
    private final EventEntityMapper eventEntityMapper;
    private final UserEntityMapper userEntityMapper;

    //with annotation @Mapper, springboot will inject object automatically
    public EventDataFetcher(EventEntityMapper eventEntityMapper, UserEntityMapper userEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
        this.userEntityMapper = userEntityMapper;
    }



    //function name match name in schema.graphqls
    @DgsQuery
    public List<Event> events() {
        //eventEntityMapper.selectList(new QueryWrapper<>()) will select all entity in DB
        List<EventEntity> eventEntityList = eventEntityMapper.selectList(new QueryWrapper<>());
//        List<Event> eventList = eventEntityList.stream()
//                .map(eventEntity -> {
//                    Event event = Event.fromEntity(eventEntity);
//                    populateEventWithUser(event, eventEntity.getCreatorID());
//                    return event;
//                }).toList();
        List<Event> eventList = eventEntityList.stream().map(Event::fromEntity).toList();
        return eventList;
    }

    //add @InputArgument before parameter: indicate it is graphQl input parameter(according to schema.graphqls)
    //change data in DB
    @DgsMutation
    public Event createEvent(@InputArgument(name = "eventInput") EventInput input) {
        EventEntity newEventEntity = EventEntity.fromEventInput(input);

        //insert this new eventEntity to postgresql DB (by mybatis)
        eventEntityMapper.insert(newEventEntity);

        Event newEvent = Event.fromEntity(newEventEntity);
        //append creatorID to the event
        //populateEventWithUser(newEvent, newEventEntity.getCreatorID());

        return newEvent;
    }

    //set event's user field, if frontend require the creator field, this method will be used automatically.(dynamic)
    //claim the which class's parameter in annotation
    @DgsData(parentType = "Event", field = "creator")
    public User creator(DgsDataFetchingEnvironment dfe) {
        Event event = dfe.getSource();
        UserEntity userEntity = userEntityMapper.selectById(event.getCreatorId());
        User user = User.fromEntity(userEntity);
        return user;
    }



        // this is traditional method
//    //set user for event by searching UserId of an Event
//    private void populateEventWithUser(Event event, Integer userId) {
//        UserEntity userEntity = userEntityMapper.selectById(userId);
//        User user = User.fromEntity(userEntity);
//        event.setCreator(user);
//    }



}
