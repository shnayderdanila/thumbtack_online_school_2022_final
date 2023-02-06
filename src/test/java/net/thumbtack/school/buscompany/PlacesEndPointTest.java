package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.RegOrderRequest;
import net.thumbtack.school.buscompany.dto.request.RegPassengerDto;
import net.thumbtack.school.buscompany.dto.request.SelectPlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.*;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlacesEndPointTest extends BaseTest{

    @Test
    public void testSelectPlace(){
        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        OrderDtoResponse order = registerOrder(clientSession, requestRegisterOrder).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123123", 1);

        SelectPlaceDtoResponse response = selectPlace(clientSession, selectPlaceDtoRequest).getBody();

        SelectPlaceDtoResponse expected = new SelectPlaceDtoResponse(order.getIdOrder(),"Билет "+trip.getIdTrip()+"_"+1,"Шнайдер", "Данила", "123123123", 1);

        assertEquals(expected, response);
    }

    @Test
    public void testSelectPlaceBusyPlace() throws JsonProcessingException {
        String adminSession  = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        OrderDtoResponse order = registerOrder(clientSession, requestRegisterOrder).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123123", 1);

        selectPlace(clientSession, selectPlaceDtoRequest).getBody();

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.BUSY_PLACE.toString(), ErrorCode.BUSY_PLACE.getField(), "Place:"+1+" is busy."));

        try {
            selectPlace(clientSession, selectPlaceDtoRequest);
        }catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));

        }
    }

    @Test
    public void testSelectPlaceIncorrectPlace() throws JsonProcessingException {
        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        OrderDtoResponse order = registerOrder(clientSession, requestRegisterOrder).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123123", 31);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_PLACE.toString(), ErrorCode.INCORRECT_PLACE.getField(), ErrorCode.INCORRECT_PLACE.getMessage()));

        try {
            selectPlace(clientSession, selectPlaceDtoRequest);
        }catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));

        }
    }

    @Test
    public void testGetFreePlaces(){
        String adminSession  = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);
        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123121"));
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123122"));
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123124"));


        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        OrderDtoResponse order = registerOrder(clientSession, requestRegisterOrder).getBody();

        FreePlacesDtoResponse fullFree = getFreePlaces(clientSession, order.getIdOrder()).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest1 = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123121", 1);
        selectPlace(clientSession, selectPlaceDtoRequest1).getBody();
        FreePlacesDtoResponse firstOrdered = getFreePlaces(clientSession, order.getIdOrder()).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest2 = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123121", 30);
        selectPlace(clientSession, selectPlaceDtoRequest2).getBody();
        FreePlacesDtoResponse changeFirstOnThirtieth  = getFreePlaces(clientSession, order.getIdOrder()).getBody();

        SelectPlaceDtoRequest selectPlaceDtoRequest3 = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123122", 2);
        SelectPlaceDtoRequest selectPlaceDtoRequest4 = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123123", 5);
        SelectPlaceDtoRequest selectPlaceDtoRequest5 = new SelectPlaceDtoRequest(order.getIdOrder(),"Шнайдер", "Данила", "123123124", 10);

        selectPlace(clientSession, selectPlaceDtoRequest3).getBody();
        selectPlace(clientSession, selectPlaceDtoRequest4).getBody();
        selectPlace(clientSession, selectPlaceDtoRequest5).getBody();
        FreePlacesDtoResponse fourPlaceOrdered  = getFreePlaces(clientSession, order.getIdOrder()).getBody();

        assertEquals(30, fullFree.getPlaces().size());
        assertEquals(29, firstOrdered.getPlaces().size());
        assertEquals(28, changeFirstOnThirtieth.getPlaces().size());
        assertEquals(25, fourPlaceOrdered.getPlaces().size());


    }

}
