package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.models.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SessionEndPointTest extends BaseTest{

    @Test
    public void testLogin(){

        String javaSessionId = registerAdminDanila();

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest("shnayderdanila", "e365025");

        String afterLoginSession = loginAdmin(loginDtoRequest).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        registerScheduledTrip(afterLoginSession);

        assertNotEquals(javaSessionId, afterLoginSession);

    }

    @Test
    public void testLoginIncorrectPassword() throws JsonProcessingException {

        registerAdminDanila();

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest("shnayderdanila", "e365025");
        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_PASSWORD.toString(), ErrorCode.WRONG_PASSWORD.getField(), ErrorCode.WRONG_PASSWORD.getMessage()));

        try {
            loginAdmin(loginDtoRequest);
        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testLoginIncorrectLogin() throws JsonProcessingException {

        registerAdminDanila();

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest("shnayder", "e365025");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_LOGIN.toString(), ErrorCode.INCORRECT_LOGIN.getField(), "User "+loginDtoRequest.getLogin()+" doesn't exist"));
        try {
            loginAdmin(loginDtoRequest);
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testLogout() throws JsonProcessingException {

        String javaSessionId = registerAdminDanila();

        logout(javaSessionId);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));
        try {
            registerScheduledTrip(javaSessionId);
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }

    @Test
    public void testLoginAfterUpdatePasswordAdmin(){

        RegAdminDtoRequest registerAdminRequest = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        String javaSessionId = registerAdmin(registerAdminRequest).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest("Данила", "Шнайдер", "Владимирович", "boss", "e365025", "e123123");

        int id = Objects.requireNonNull(updateAdmin(javaSessionId, updateAdminDtoRequest).getBody()).getId();

        logout(javaSessionId);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest("shnayderdanila", "e123123");

        ResponseEntity<AdminDtoResponse> response = loginAdmin(loginDtoRequest);

        AdminDtoResponse expected = new AdminDtoResponse(
                id, "Данила", "Шнайдер", "Владимирович", UserType.ADMIN.toString(), "boss");

        assertEquals(expected, response.getBody());
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

    }

    @Test
    public void testLoginAfterUpdatePasswordClient(){

        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        String javaSessionId = registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder123@mail.ru", "+78005553535", "e365025", "e123123");

        int id = updateClient(javaSessionId, updateClientDtoRequest).getBody().getId();

        logout(javaSessionId);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest("shnayderdanila", "e123123");

        ResponseEntity<ClientDtoResponse> response = loginClient(loginDtoRequest);

        ClientDtoResponse expected = new ClientDtoResponse(
                id, "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "shnayder123@mail.ru", "+78005553535");

        assertEquals(expected, response.getBody());
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

    }

}
