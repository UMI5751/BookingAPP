package com.example.BookingAPP.fetcher;

import com.example.BookingAPP.custom.AuthContext;
import com.example.BookingAPP.entity.BookingEntity;
import com.example.BookingAPP.entity.EventEntity;
import com.example.BookingAPP.entity.UserEntity;
import com.example.BookingAPP.mapper.BookingEntityMapper;
import com.example.BookingAPP.mapper.EventEntityMapper;
import com.example.BookingAPP.type.Booking;
import com.example.BookingAPP.type.Event;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.context.DgsContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@DgsComponent
public class BookingDataFetcher {
    private final BookingEntityMapper bookingEntityMapper;
    private final EventEntityMapper eventEntityMapper;

    public BookingDataFetcher(BookingEntityMapper bookingEntityMapper, EventEntityMapper eventEntityMapper) {
        this.bookingEntityMapper = bookingEntityMapper;
        this.eventEntityMapper = eventEntityMapper;
    }


    //keep same name as in schema, implement bookEvent fetcher in schema
    @DgsMutation
    public Booking bookEvent(@InputArgument  String eventId, DataFetchingEnvironment dfe) {

        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        UserEntity userEntity = authContext.getUserEntity();

        //create bookingEntity from user and event
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setUserId((userEntity.getId()));
        bookingEntity.setEventId(Integer.parseInt(eventId));
        bookingEntity.setCreatedAt(new Date());
        bookingEntity.setUpdatedAt(new Date());

        bookingEntityMapper.insert(bookingEntity);

        Booking booking = Booking.fromEntity(bookingEntity);
        return booking;

    }

    @DgsQuery
    public List<Booking> bookings() {
        List<Booking> bookings = bookingEntityMapper.selectList(null).stream()
                .map(Booking::fromEntity).toList();
        return bookings;
    }

    @DgsMutation
    public Event cancelBooking(@InputArgument("bookingId") String bookingIdString, DataFetchingEnvironment dfe) {
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        Integer bookingId = Integer.parseInt(bookingIdString);

        BookingEntity bookingEntity = bookingEntityMapper.selectById(bookingId);
        if (bookingEntity == null) {
            throw new RuntimeException(String.format("Booking with id %s does not exists", bookingIdString));
        }

        Integer userId = bookingEntity.getUserId();
        UserEntity userEntity = authContext.getUserEntity();
        if (!userEntity.getId().equals(userId)) {
            throw new RuntimeException("you are not allowed to cancel other people's booking");
        }

        bookingEntityMapper.deleteById(bookingId);

        Integer eventId = bookingEntity.getEventId();
        EventEntity eventEntity = eventEntityMapper.selectById(eventId);
        Event event = Event.fromEntity(eventEntity);

        return event;
    }



}
