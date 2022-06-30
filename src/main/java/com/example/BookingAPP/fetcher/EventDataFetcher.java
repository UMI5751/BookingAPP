package com.example.BookingAPP.fetcher;

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

//implement query in schema.graphqls, this is a solver(fetcher)
@DgsComponent
public class EventDataFetcher {
    private List<Event> events = new ArrayList<>();

    //function name match name in schema.graphqls
    @DgsQuery
    public List<Event> events() {
        return events;
    }

    //add @InputArgument before parameter: graphQl input parameter
    @DgsMutation
    public Event createEvent(@InputArgument(name = "eventInput") EventInput input) {
        Event newEvent = new Event();
        newEvent.setId(UUID.randomUUID().toString());
        newEvent.setTitle(input.getTitle());
        newEvent.setDescription(input.getDescription());
        newEvent.setPrice(input.getPrice());
        newEvent.setDate(input.getDate());

        events.add(newEvent);
        return newEvent;
    }
}
