package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.models.Bus;
import net.thumbtack.school.buscompany.models.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripEndPointTest extends BaseTest{

    @Test
    public void testRegisterScheduledTrip(){

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "MONDAY, SATURDAY");
        Schedule schedule2 =new Schedule("2022-06-01", "2022-06-08", "1, 2, 3");
        Schedule schedule3 =new Schedule("2022-06-01", "2022-06-08", "odd");


        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule1, null);

        TripDtoRequest tripDtoRequest2 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule2, null);

        TripDtoRequest tripDtoRequest3 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule3, null);

        TripDtoResponse response1 = registerTrip(javaSessionIdAdmin, tripDtoRequest1).getBody();
        TripDtoResponse response2 = registerTrip(javaSessionIdAdmin, tripDtoRequest2).getBody();
        TripDtoResponse response3 = registerTrip(javaSessionIdAdmin, tripDtoRequest3).getBody();

        List<String> weekDate = new ArrayList<>(Arrays.asList("2022-06-04","2022-06-06"));
        List<String> dayDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-02", "2022-06-03"));
        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));

        Bus bus = new Bus(1, "Автобус", 30);

        TripDtoResponse expected1 = new TripDtoResponse(response1.getIdTrip(), "Омск", "Таврическое",
                                                        "13:45", "01:00", 150.0, bus, false, schedule1, weekDate);

        TripDtoResponse expected2 = new TripDtoResponse(response2.getIdTrip(), "Омск", "Таврическое",
                                                        "13:45", "01:00", 150.0, bus, false, schedule2, dayDate);

        TripDtoResponse expected3 = new TripDtoResponse(response3.getIdTrip(), "Омск", "Таврическое",
                                                        "13:45", "01:00", 150.0, bus, false, schedule3, oddDate);

        assertEquals(expected1, response1);
        assertEquals(expected2, response2);
        assertEquals(expected3, response3);

    }

    @Test
    public void testRegisterDateTrip(){

        String javaSessionIdAdmin = registerAdminDanila();

        List<String> weekDate = new ArrayList<>(Arrays.asList("2022-06-04","2022-06-06"));
        List<String> dayDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-02", "2022-06-03"));
        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, null, weekDate);

        TripDtoRequest tripDtoRequest2 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, null, dayDate);

        TripDtoRequest tripDtoRequest3 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, null, oddDate);

        TripDtoResponse response1 = registerTrip(javaSessionIdAdmin, tripDtoRequest1).getBody();
        TripDtoResponse response2 = registerTrip(javaSessionIdAdmin, tripDtoRequest2).getBody();
        TripDtoResponse response3 = registerTrip(javaSessionIdAdmin, tripDtoRequest3).getBody();


        Bus bus = new Bus(1, "Автобус", 30);

        TripDtoResponse expected1 = new TripDtoResponse(response1.getIdTrip(), "Омск", "Таврическое",
                "13:45", "01:00", 150.0, bus, false, null, weekDate);

        TripDtoResponse expected2 = new TripDtoResponse(response2.getIdTrip(), "Омск", "Таврическое",
                "13:45", "01:00", 150.0, bus, false, null, dayDate);

        TripDtoResponse expected3 = new TripDtoResponse(response3.getIdTrip(), "Омск", "Таврическое",
                "13:45", "01:00", 150.0, bus, false, null, oddDate);

        assertEquals(expected1, response1);
        assertEquals(expected2, response2);
        assertEquals(expected3, response3);

    }

    @Test
    public void testRegisterTripBothScheduleAndDate() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 = new Schedule("2022-06-01", "2022-06-08", "MON, SAT");

        List<String> weekDate = new ArrayList<>(Arrays.asList("2022-06-04","2022-06-06"));

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, weekDate);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "dates", "Incorrect filling Trip. One of two must be specified schedule or dates"));
        expected.getErrors().add(new Error("Trip", "schedule", "Incorrect filling Trip. One of two must be specified schedule or dates"));

        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1).getBody();
            fail();
        }
        catch (HttpClientErrorException e){
            ErrorDtoResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected.getErrors().size(), response.getErrors().size());
            assertTrue(expected.getErrors().containsAll(response.getErrors()));

        }

    }



    @Test
    public void testRegisterScheduleTripIncorrectDate1() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("20220601", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "The dates:2022-06-08,20220601 must be in the format yyyy-mm-dd"));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectDate2() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("01-06-2022", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "The dates:2022-06-08,01-06-2022 must be in the format yyyy-mm-dd"));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectDate3() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("01062022", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "The dates:2022-06-08,01062022 must be in the format yyyy-mm-dd"));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectDate4() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022.06.08", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "The dates:2022-06-08,2022.06.08 must be in the format yyyy-mm-dd"));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectDate5() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-15-08", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "The dates:2022-06-08,2022-15-08 must be in the format yyyy-mm-dd"));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectDate6() throws JsonProcessingException {
        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-12", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "schedule", "Departure date:"+schedule1.getFromDate()+" should be before then arrival date:"+schedule1.getToDate()));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectPeriod() throws JsonProcessingException {
        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "20, 30");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_SCHEDULE_PERIOD.toString(),
                                           ErrorCode.INCORRECT_SCHEDULE_PERIOD.getField(),
                                  "The period " + schedule1.getPeriod() +" consists of an empty list of dates."));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }
    }

    @Test
    public void testRegisterScheduleTripIncorrectPeriod1() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "MN, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_SCHEDULE_PERIOD.toString(), ErrorCode.INCORRECT_SCHEDULE_PERIOD.getField(), "Incorrect period:"+schedule1.getPeriod()));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterScheduleTripIncorrectPeriod2() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "1, SAT");

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_SCHEDULE_PERIOD.toString(), ErrorCode.INCORRECT_SCHEDULE_PERIOD.getField(), "Incorrect period:"+schedule1.getPeriod()));


        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }



    @Test
    public void testRegisterDateTripIncorrectDate() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        List<String> weekDate = new ArrayList<>(Arrays.asList("2022-06-04","20220606"));

        TripDtoRequest tripDtoRequest1 = new TripDtoRequest("Автобус", "Омск", "Таврическое",
                                                            "13:45", "01:00", 150.0, null, weekDate);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Trip", "dates", "The dates:"+weekDate+" must be in the format yyyy-mm-dd"));

        try {
            registerTrip(javaSessionIdAdmin, tripDtoRequest1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testRegisterTripIncorrectBus() throws JsonProcessingException {

        String javaSessionAdmin = registerAdminDanila();

        Schedule schedule1 =new Schedule("2022-06-01", "2022-06-08", "MON, SAT");

        TripDtoRequest tripDtoRequest = new TripDtoRequest("порше", "Омск", "Таврическое",
                                                           "13:45", "01:00", 150.0, schedule1, null);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_BUS_NAME.toString(), ErrorCode.INCORRECT_BUS_NAME.getField(), "Bus:порше doesn't exist"));

        try {
            registerTrip(javaSessionAdmin, tripDtoRequest);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }
    }

    @Test
    public void testRegisterTripIncorrectJavaSessionId() throws JsonProcessingException {

        String javaSessionClient = registerClientIvan();

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            registerScheduledTrip(javaSessionClient);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }
    }


    @Test
    public void testUpdateTrip(){

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);

        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));

        TripDtoRequest update = new TripDtoRequest("Газель", "Таврическое", "Омск",
                "13:45", "01:00", 150.0, null, oddDate);

        Bus bus = new Bus(2, "Газель", 12);

        TripDtoResponse expected = new TripDtoResponse(trip.getIdTrip(), "Таврическое", "Омск",
                                                      "13:45", "01:00", 150.0, bus, false, null, oddDate);

        TripDtoResponse response = updateTrip(javaSessionIdAdmin, trip.getIdTrip(), update).getBody();

        assertEquals(expected, response);

    }

    @Test
    public void testUpdateTripIncorrectId() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);

        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));

        TripDtoRequest update = new TripDtoRequest("Газель", "Таврическое", "Омск",
                "13:45", "01:00", 150.0, null, oddDate);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ID_TRIP.toString(), ErrorCode.INCORRECT_ID_TRIP.getField(), "Incorrect id Trip:"+(trip.getIdTrip()-1)));

        try {
            updateTrip(javaSessionIdAdmin, trip.getIdTrip()-1, update);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testUpdateTripIncorrectSession() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();
        String javaSessionIdClient = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);

        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));
        TripDtoRequest update = new TripDtoRequest("Газель", "Таврическое", "Омск",
                "13:45", "01:00", 150.0, null, oddDate);


        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            updateTrip(javaSessionIdClient, trip.getIdTrip(), update).getBody();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }


    }

    @Test
    public void testGetTripById() {

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);
        trip.setStart("13:45:00");
        trip.setDuration("01:00:00");

        assertEquals(trip, getTripById(javaSessionIdAdmin, trip.getIdTrip()).getBody());

    }

    @Test
    public void testGetTripByIdIncorrectId() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ID_TRIP.toString(), ErrorCode.INCORRECT_ID_TRIP.getField(), "Incorrect id Trip:"+(trip.getIdTrip()-1)));

        try{
            getTripById(javaSessionIdAdmin, trip.getIdTrip()-1);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testGetTripByIdIncorrectSession() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();
        String javaSessionIdClient = registerClientIvan();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);
        trip.setStart("13:45:00");
        trip.setDuration("01:00:00");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            getTripById(javaSessionIdClient, trip.getIdTrip());
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testDeleteTrip() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();
        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);

        deleteTrip(javaSessionIdAdmin, trip.getIdTrip());

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ID_TRIP.toString(), ErrorCode.INCORRECT_ID_TRIP.getField(), "Incorrect id Trip:"+trip.getIdTrip()));

        try {
            getTripById(javaSessionIdAdmin, trip.getIdTrip());
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testDeleteTripIncorrectId() throws JsonProcessingException {
        String javaSessionIdAdmin = registerAdminDanila();
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ID_TRIP.toString(), ErrorCode.INCORRECT_ID_TRIP.getField(), "Incorrect id Trip:0"));

        try {
            getTripById(javaSessionIdAdmin, 0);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testDeleteTripIncorrectSession() throws JsonProcessingException {
        String javaSessionIdAdmin = registerClientIvan();
        TripDtoResponse trip = registerScheduledTrip(registerAdminDanila());

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            getTripById(javaSessionIdAdmin, trip.getIdTrip());
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }
    }

    @Test
    public void testApproveTrip(){
        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);

        trip.setStart("13:45:00");
        trip.setDuration("01:00:00");
        trip.setApproved(true);

        approveTrip(javaSessionIdAdmin, trip.getIdTrip()).getBody();
        TripDtoResponse response = getTripById(javaSessionIdAdmin, trip.getIdTrip()).getBody();
        assertEquals(trip, response);

    }

    @Test
    public void testUpdateApprovedTrip() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);
        approveTrip(javaSessionIdAdmin, trip.getIdTrip());


        List<String> oddDate  = new ArrayList<>(Arrays.asList("2022-06-01","2022-06-03", "2022-06-05", "2022-06-07"));
        TripDtoRequest update = new TripDtoRequest("Газель", "Таврическое", "Омск",
                "13:45", "01:00", 150.0, null, oddDate);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_ACTION.toString(), ErrorCode.WRONG_ACTION.getField(), "You can not delete/update approved Trip:"+trip.getIdTrip()));

        try {
            updateTrip(javaSessionIdAdmin, trip.getIdTrip(), update);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testDeleteApprovedTrip() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        TripDtoResponse trip = registerScheduledTrip(javaSessionIdAdmin);
        approveTrip(javaSessionIdAdmin, trip.getIdTrip());

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_ACTION.toString(), ErrorCode.WRONG_ACTION.getField(), "You can not delete/update approved Trip:"+trip.getIdTrip()));

        try {
            deleteTrip(javaSessionIdAdmin, trip.getIdTrip());
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testGetTripsWithParam(){

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
        approveTrip(javaSessionIdAdmin, trip6.getIdTrip());


        GetTripParamsDtoRequest getAllTrip = new GetTripParamsDtoRequest(                null,         null,         null,        null,        null);
        GetTripParamsDtoRequest getFromOmsk = new GetTripParamsDtoRequest(               "Омск",       null,         null,        null,        null);
        GetTripParamsDtoRequest getToTavricheskoeAndGazel  = new GetTripParamsDtoRequest(null,         "Таврическое",null,        null,        "Газель");
        GetTripParamsDtoRequest getFromDate = new GetTripParamsDtoRequest(               null,         null,         "2022-06-01",null,        null);
        GetTripParamsDtoRequest getToDate = new GetTripParamsDtoRequest(                 null,         null,         null,        "2022-06-08",null);
        GetTripParamsDtoRequest getWithAllParam = new GetTripParamsDtoRequest(           "Таврическое","Омск",       "2022-06-01","2022-06-08","Газель");

        //запросы с сессией админа
        int countAllTrip                = getTripsWithParam(javaSessionIdAdmin, getAllTrip).getBody().getList().size();
        int countGetFromOmsk            = getTripsWithParam(javaSessionIdAdmin, getFromOmsk).getBody().getList().size();
        int countToTavricheskoeAndGazel = getTripsWithParam(javaSessionIdAdmin, getToTavricheskoeAndGazel).getBody().getList().size();
        int countFromDate               = getTripsWithParam(javaSessionIdAdmin, getFromDate).getBody().getList().size();
        int countToDate                 = getTripsWithParam(javaSessionIdAdmin, getToDate).getBody().getList().size();
        int countWithAllParam           = getTripsWithParam(javaSessionIdAdmin, getWithAllParam).getBody().getList().size();


        assertEquals(6, countAllTrip);
        assertEquals(4, countGetFromOmsk);
        assertEquals(1, countToTavricheskoeAndGazel);
        assertEquals(2, countFromDate);
        assertEquals(3, countToDate);
        assertEquals(1, countWithAllParam);

        //запросы с сессией пользователя
        String javaSessionIdClient = registerClientIvan();

        countAllTrip                = getTripsWithParam(javaSessionIdClient, getAllTrip).getBody().getList().size();
        countGetFromOmsk            = getTripsWithParam(javaSessionIdClient, getFromOmsk).getBody().getList().size();
        countToTavricheskoeAndGazel = getTripsWithParam(javaSessionIdClient, getToTavricheskoeAndGazel).getBody().getList().size();
        countFromDate               = getTripsWithParam(javaSessionIdClient, getFromDate).getBody().getList().size();
        countToDate                 = getTripsWithParam(javaSessionIdClient, getToDate).getBody().getList().size();
        countWithAllParam           = getTripsWithParam(javaSessionIdClient, getWithAllParam).getBody().getList().size();

        assertEquals(3, countAllTrip);
        assertEquals(2, countGetFromOmsk);
        assertEquals(1, countToTavricheskoeAndGazel);
        assertEquals(1, countFromDate);
        assertEquals(2, countToDate);
        assertEquals(1, countWithAllParam);

    }

    @Test
    public void testGetTripsWithIncorrectParam() throws JsonProcessingException {

        String javaSessionIdAdmin = registerAdminDanila();

        GetTripParamsDtoRequest getWithAllParam = new GetTripParamsDtoRequest("Таврическое","Омск","20220601","2022-06-08","Газель");
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Date", "fromDate", "Date:"+getWithAllParam.getFromDate()+" should be yyyy-mm-dd"));


        try {
            getTripsWithParam(javaSessionIdAdmin, getWithAllParam);
            fail();
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testGetTripsWithParamIncorrectSession() throws JsonProcessingException {
        String javaSessionIdAdmin = registerAdminDanila();

        GetTripParamsDtoRequest getAllTrip = new GetTripParamsDtoRequest(null,null,null,null,null);
        getTripsWithParam(javaSessionIdAdmin, getAllTrip);

        logout(javaSessionIdAdmin);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));
        try {
            getTripsWithParam(javaSessionIdAdmin, getAllTrip);
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }
    }


}
