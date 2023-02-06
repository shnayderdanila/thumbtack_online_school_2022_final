package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.models.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderEndPointTest extends BaseTest{

    @Test
    public void testRegisterOrder(){

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);
        trip.setStart("13:45:00");
        trip.setDuration("01:00:00");

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        OrderDtoResponse order = registerOrder(clientSession, requestRegisterOrder).getBody();

        OrderDtoResponse expected = new OrderDtoResponse(order.getIdOrder(), trip.getIdTrip(), trip.getFromStation(), trip.getToStation(),
                                                        trip.getBus().getBusName(),"2022-06-01", trip.getStart(), trip.getDuration(),
                                                        trip.getPrice(), trip.getPrice()*passengers.size(), order.getPassengers());

        assertEquals(expected, order);


    }

    @Test
    public void testRegisterOrderIncorrectDate() throws JsonProcessingException {

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022.06.01", passengers);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Date", "date", "Date:"+requestRegisterOrder.getDate()+" should be yyyy-mm-dd"));

        try {
            registerOrder(clientSession, requestRegisterOrder);
            fail();

        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterOrderIncorrectPassengers() throws JsonProcessingException {

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));
        passengers.add(new RegPassengerDto("Стрельников", "Иван", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Passport", "passengers", "Passport passengers should be unique:"+passengers));

        try {
            registerOrder(clientSession, requestRegisterOrder);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterOrderIncorrectTripId() throws JsonProcessingException {

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip()-1, "2022-06-01", passengers);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ID_TRIP.toString(), ErrorCode.INCORRECT_ID_TRIP.getField(), "Incorrect id Trip:"+(trip.getIdTrip()-1)));

        try {
            registerOrder(clientSession, requestRegisterOrder);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterOrderIncorrectSession() throws JsonProcessingException {

        String adminSession = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip()-1, "2022-06-01", passengers);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            registerOrder(adminSession, requestRegisterOrder);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterOrderNonApprovedTrip() throws JsonProcessingException {

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);


        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_ACTION.toString(), ErrorCode.WRONG_ACTION.getField(), "You can not register on un approved Trip:"+trip.getIdTrip()));

        try {
            registerOrder(clientSession, requestRegisterOrder);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void registerOrderMaxPlace() throws JsonProcessingException {

        String adminSession = registerAdminDanila();
        String clientSession = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(adminSession);

        approveTrip(adminSession, trip.getIdTrip());

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder = new RegOrderRequest(trip.getIdTrip(), "2022-06-01", passengers);

        for (int i = 0; i < 30; i++){
            registerOrder(clientSession, requestRegisterOrder);
        }

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.ALL_PLACE_BUSY.toString(), ErrorCode.ALL_PLACE_BUSY.getField(), ErrorCode.ALL_PLACE_BUSY.getMessage()));

        try {
            registerOrder(clientSession, requestRegisterOrder);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }


    }


    @Test
    public void testGetOrdersWithParam(){

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "MONDAY, SATURDAY");
        Schedule schedule2 =new Schedule("2022-06-02", "2022-06-08", "1, 2, 3");
        Schedule schedule3 =new Schedule("2022-06-01", "2022-06-08", "odd");

        List<String> weekDate = new ArrayList<>(Arrays.asList("2022-06-04","2022-06-06"));
        List<String> dayDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-02", "2022-06-03"));
        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Газель", "Таврическое", "Омск",
                                                            "13:45", "01:00", 150.0, schedule1, null);

        TripDtoRequest tripDtoRequest2 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule2, null);

        TripDtoRequest tripDtoRequest3 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule3, null);

        TripDtoRequest tripDtoRequest4 = new TripDtoRequest("Автобус", "Таврическое", "Омск",
                                                            "13:45", "01:00", 150.0, null, weekDate);

        TripDtoRequest tripDtoRequest5 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, null, dayDate);

        TripDtoRequest tripDtoRequest6 = new TripDtoRequest("Газель", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, null, oddDate);

        TripDtoResponse trip1 = registerTrip(javaSessionIdAdmin, tripDtoRequest1).getBody();
        TripDtoResponse trip2 = registerTrip(javaSessionIdAdmin, tripDtoRequest2).getBody();
        TripDtoResponse trip3 = registerTrip(javaSessionIdAdmin, tripDtoRequest3).getBody();
        TripDtoResponse trip4 = registerTrip(javaSessionIdAdmin, tripDtoRequest4).getBody();
        TripDtoResponse trip5 = registerTrip(javaSessionIdAdmin, tripDtoRequest5).getBody();
        TripDtoResponse trip6 = registerTrip(javaSessionIdAdmin, tripDtoRequest6).getBody();

        approveTrip(javaSessionIdAdmin, trip1.getIdTrip());
        approveTrip(javaSessionIdAdmin, trip2.getIdTrip());
        approveTrip(javaSessionIdAdmin, trip3.getIdTrip());
        approveTrip(javaSessionIdAdmin, trip4.getIdTrip());
        approveTrip(javaSessionIdAdmin, trip5.getIdTrip());
        approveTrip(javaSessionIdAdmin, trip6.getIdTrip());

        RegClientDtoRequest regClientDtoRequest1 = new RegClientDtoRequest(
                "ivanstrelnikov", "e365025", "Иван", "Стрельников", "Витальевич", "str@mail.ru","89836232211");
        RegClientDtoRequest regClientDtoRequest2 = new RegClientDtoRequest(
                "maximshnayder", "e365025", "Максим", "Шнайдер", "Владимирович", "mvs@mail.ru","89836232211");

        ResponseEntity<ClientDtoResponse> responseClient1 = registerClient(regClientDtoRequest1);
        ResponseEntity<ClientDtoResponse> responseClient2 = registerClient(regClientDtoRequest2);

        int idIvan  = responseClient1.getBody().getId();
        int idMaxim = responseClient2.getBody().getId();

        String clientIvanSession =  responseClient1.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        String clientMaximSession = responseClient2.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        List<RegPassengerDto> passengers = new ArrayList<>();
        passengers.add(new RegPassengerDto("Шнайдер", "Данила", "123123123"));

        RegOrderRequest requestRegisterOrder1 = new RegOrderRequest(trip1.getIdTrip(), "2022-06-04", passengers);
        RegOrderRequest requestRegisterOrder2 = new RegOrderRequest(trip2.getIdTrip(), "2022-06-02", passengers);
        RegOrderRequest requestRegisterOrder3 = new RegOrderRequest(trip3.getIdTrip(), "2022-06-01", passengers);

        RegOrderRequest requestRegisterOrder4 = new RegOrderRequest(trip4.getIdTrip(), "2022-06-04", passengers);
        RegOrderRequest requestRegisterOrder5 = new RegOrderRequest(trip5.getIdTrip(), "2022-06-02", passengers);
        RegOrderRequest requestRegisterOrder6 = new RegOrderRequest(trip6.getIdTrip(), "2022-06-01", passengers);

        registerOrder(clientIvanSession,  requestRegisterOrder1).getBody();
        registerOrder(clientIvanSession,  requestRegisterOrder2).getBody();
        registerOrder(clientIvanSession,  requestRegisterOrder3).getBody();

        registerOrder(clientMaximSession, requestRegisterOrder4).getBody();
        registerOrder(clientMaximSession, requestRegisterOrder5).getBody();
        registerOrder(clientMaximSession, requestRegisterOrder6).getBody();

        GetOrderParamsDtoRequest getAllOrder = new GetOrderParamsDtoRequest(               null,         null,         null,        null,        null,     null);

        GetOrderParamsDtoRequest getFromOmsk1 = new GetOrderParamsDtoRequest(               "Омск",       null,         null,        null,        null,     idIvan);
        GetOrderParamsDtoRequest getFromOmsk2 = new GetOrderParamsDtoRequest(               "Омск",       null,         null,        null,        null,     idMaxim);

        GetOrderParamsDtoRequest getToTavricheskoeAndGazel1  = new GetOrderParamsDtoRequest(null,         "Таврическое",null,        null,        "Газель", idIvan);
        GetOrderParamsDtoRequest getToTavricheskoeAndGazel2  = new GetOrderParamsDtoRequest(null,         "Таврическое",null,        null,        "Газель", idMaxim);

        GetOrderParamsDtoRequest getFromDate1 = new GetOrderParamsDtoRequest(               null,         null,         "2022-06-01",null,        null,     idIvan);
        GetOrderParamsDtoRequest getFromDate2 = new GetOrderParamsDtoRequest(               null,         null,         "2022-06-01",null,        null,     idMaxim);

        GetOrderParamsDtoRequest getToDate1 = new GetOrderParamsDtoRequest(                 null,         null,         null,        "2022-06-08",null,     idIvan);
        GetOrderParamsDtoRequest getToDate2 = new GetOrderParamsDtoRequest(                 null,         null,         null,        "2022-06-08",null,     idMaxim);

        GetOrderParamsDtoRequest getWithAllParam1 = new GetOrderParamsDtoRequest(           "Таврическое","Омск",       "2022-06-01","2022-06-08","Газель", idIvan);
        GetOrderParamsDtoRequest getWithAllParam2 = new GetOrderParamsDtoRequest(           "Таврическое","Омск",       "2022-06-01","2022-06-08","Газель", idMaxim);

        //запросы с сессией админа
        int countAllTrip                 = getOrdersWithParam(javaSessionIdAdmin, getAllOrder).getBody().getList().size();

        int countGetFromOmsk1            = getOrdersWithParam(javaSessionIdAdmin, getFromOmsk1).getBody().getList().size();
        int countGetFromOmsk2            = getOrdersWithParam(javaSessionIdAdmin, getFromOmsk2).getBody().getList().size();

        int countToTavricheskoeAndGazel1 = getOrdersWithParam(javaSessionIdAdmin, getToTavricheskoeAndGazel1).getBody().getList().size();
        int countToTavricheskoeAndGazel2 = getOrdersWithParam(javaSessionIdAdmin, getToTavricheskoeAndGazel2).getBody().getList().size();

        int countFromDate1               = getOrdersWithParam(javaSessionIdAdmin, getFromDate1).getBody().getList().size();
        int countFromDate2               = getOrdersWithParam(javaSessionIdAdmin, getFromDate2).getBody().getList().size();

        int countToDate1                 = getOrdersWithParam(javaSessionIdAdmin, getToDate1).getBody().getList().size();
        int countToDate2                 = getOrdersWithParam(javaSessionIdAdmin, getToDate2).getBody().getList().size();

        int countWithAllParam1           = getOrdersWithParam(javaSessionIdAdmin, getWithAllParam1).getBody().getList().size();
        int countWithAllParam2           = getOrdersWithParam(javaSessionIdAdmin, getWithAllParam2).getBody().getList().size();

        assertAll(
                ()->{
                    assertEquals(6,countAllTrip);

                    assertEquals(getOrdersWithParam(clientIvanSession, getFromOmsk2).getBody().getList().size()                ,countGetFromOmsk1);
                    assertEquals(getOrdersWithParam(clientIvanSession, getToTavricheskoeAndGazel2).getBody().getList().size()  ,countToTavricheskoeAndGazel1);
                    assertEquals(getOrdersWithParam(clientIvanSession, getFromDate2).getBody().getList().size()                ,countFromDate1);
                    assertEquals(getOrdersWithParam(clientIvanSession, getToDate2).getBody().getList().size()                  ,countToDate1);
                    assertEquals(getOrdersWithParam(clientIvanSession, getWithAllParam2).getBody().getList().size()            ,countWithAllParam1);

                    assertEquals(getOrdersWithParam(clientMaximSession, getFromOmsk2).getBody().getList().size()               ,countGetFromOmsk2);
                    assertEquals(getOrdersWithParam(clientMaximSession, getToTavricheskoeAndGazel2).getBody().getList().size() ,countToTavricheskoeAndGazel2);
                    assertEquals(getOrdersWithParam(clientMaximSession, getFromDate2).getBody().getList().size()               ,countFromDate2);
                    assertEquals(getOrdersWithParam(clientMaximSession, getToDate2).getBody().getList().size()                 ,countToDate2);
                    assertEquals(getOrdersWithParam(clientMaximSession, getWithAllParam2).getBody().getList().size()           ,countWithAllParam2);
                }
        );

    }



}
