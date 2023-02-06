package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.RegAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListBusDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BusEndPointTest extends BaseTest{

    @Test
    public void testGetAllBus() {

        RegAdminDtoRequest registerAdmin = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        String javaSessionId = registerAdmin(registerAdmin).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];


        ListBusDtoResponse expected = new ListBusDtoResponse(new ArrayList<>());
        expected.getList().add(new BusDtoResponse("Автобус", 30));
        expected.getList().add(new BusDtoResponse("Газель", 12));
        expected.getList().add(new BusDtoResponse("Двухэтажный автобус", 60));
        expected.getList().add(new BusDtoResponse("Микро автобус", 16));
        expected.getList().sort(Comparator.comparing(BusDtoResponse::getBusName));

        ListBusDtoResponse response = getAllBuses(javaSessionId).getBody();
        response.getList().sort(Comparator.comparing(BusDtoResponse::getBusName));

        assertEquals(expected, response);

    }

    @Test
    public void testGetAllBusWrongJavaSessionId() throws JsonProcessingException {

        RegClientDtoRequest request = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");


        String javaSessionId = registerClient(request).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];


        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {

            getAllBuses(javaSessionId);
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }
}
