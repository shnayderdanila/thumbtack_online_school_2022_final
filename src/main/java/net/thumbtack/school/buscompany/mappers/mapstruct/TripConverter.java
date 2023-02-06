package net.thumbtack.school.buscompany.mappers.mapstruct;

import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.models.Trip;
import net.thumbtack.school.buscompany.models.TripDate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TripConverter {
    TripConverter MAPPER = Mappers.getMapper(TripConverter.class);

    default Trip dtoToTrip(TripDtoRequest request){

        Trip trip = new Trip();
        trip.setApproved( false );
        trip.setDuration( request.getDuration() );
        trip.setStart( request.getStart() );
        trip.setPrice( request.getPrice() );
        trip.setSchedule( request.getSchedule() );
        trip.setFromStation( request.getFromStation() );
        trip.setToStation( request.getToStation() );

        if (request.getDates() != null) {
            List<TripDate> dates = new ArrayList<>();
            request.getDates().forEach(
                    x -> dates.add(new TripDate(0, trip, x))
            );
            trip.setDates( dates );
        }

        return trip;
    }


    default TripDtoResponse tripToDto(Trip trip){
        TripDtoResponse response = new TripDtoResponse();

        response.setIdTrip( trip.getId() );
        response.setToStation( trip.getToStation() );
        response.setFromStation( trip.getFromStation() );
        response.setStart( trip.getStart() );
        response.setDuration( trip.getDuration() );
        response.setPrice( trip.getPrice() );
        response.setBus( trip.getBus() );
        response.setApproved( trip.isApproved() );
        response.setSchedule(trip.getSchedule());

        List<String> dates = new ArrayList<>();
        trip.getDates().forEach(x -> dates.add(
                x.getDate())
        );
        response.setDates(dates);

        return response;
    }

    default void update(Trip trip, TripDtoRequest request){

        trip.setApproved(false);
        trip.setDuration( request.getDuration() );
        trip.setStart( request.getStart() );
        trip.setPrice( request.getPrice() );
        trip.setSchedule( request.getSchedule() );
        trip.setFromStation( request.getFromStation() );
        trip.setToStation( request.getToStation() );

        if (request.getDates() != null) {
            List<TripDate> dates = new ArrayList<>();
            request.getDates().forEach(
                    x -> dates.add(new TripDate(0, trip, x))
            );
            trip.setDates( dates );
        }

    }

}
