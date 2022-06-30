package com.example.BookingAPP.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.mapper.EventEntityMapper;
import com.example.BookingAPP.type.Event;
import com.example.BookingAPP.type.EventInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

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

    //with annotation @Mapper, springboot will inject object automatically
    public EventDataFetcher(EventEntityMapper eventEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
    }



    //function name match name in schema.graphqls
    @DgsQuery
    public List<Event> events() {
        //eventEntityMapper.selectList(new QueryWrapper<>()) will select all entity in DB
        List<EventEntity> eventEntityList = eventEntityMapper.selectList(new QueryWrapper<>());
        List<Event> eventList = eventEntityList.stream().map(Event::fromEntity).toList();
        return eventList;
    }

    //add @InputArgument before parameter: indicate it is graphQl input parameter(according to schema.graphqls)
    //change data in DB
    @DgsMutation
    public Event createEvent(@InputArgument(name = "eventInput") EventInput input) {
        EventEntity eventEntity = EventEntity.fromEventInput(input);

        //insert this new eventEntity to postgresql DB (by mybatis)
        eventEntityMapper.insert(eventEntity);

        return Event.fromEntity(eventEntity);
    }
}
