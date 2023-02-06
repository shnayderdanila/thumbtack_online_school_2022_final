package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateClientDtoRequest;
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

public class ClientEndPointTest extends BaseTest {

    @Test
    public void testRegisterClient(){

        RegClientDtoRequest request = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        ResponseEntity<ClientDtoResponse> response = registerClient(request);

        ClientDtoResponse expected = new ClientDtoResponse(
                Objects.requireNonNull(response.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "shnayder@mail.ru", "88005553535");

        assertEquals(expected, response.getBody());
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

    }

    @Test
    public void testRegisterClientCorrectNumber(){

        RegClientDtoRequest request1 = new RegClientDtoRequest("shnayderdanila1", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder1@mail.ru", "+78005553535");
        RegClientDtoRequest request2 = new RegClientDtoRequest("shnayderdanila2", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder2@mail.ru", "88005553535");
        RegClientDtoRequest request3 = new RegClientDtoRequest("shnayderdanila3", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder3@mail.ru", "8-800-555-35-35");

        ResponseEntity<ClientDtoResponse> response1 = registerClient(request1);
        ResponseEntity<ClientDtoResponse> response2 = registerClient(request2);
        ResponseEntity<ClientDtoResponse> response3 = registerClient(request3);

        ClientDtoResponse expected1 = new ClientDtoResponse(
                Objects.requireNonNull(response1.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "shnayder1@mail.ru", "+78005553535");
        ClientDtoResponse expected2 = new ClientDtoResponse(
                Objects.requireNonNull(response2.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "shnayder2@mail.ru", "88005553535");
        ClientDtoResponse expected3 = new ClientDtoResponse(
                Objects.requireNonNull(response3.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "shnayder3@mail.ru", "8-800-555-35-35");

        assertEquals(expected1, response1.getBody());
        assertEquals(expected2, response2.getBody());
        assertEquals(expected3, response3.getBody());
        assertNotNull(response1.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertNotNull(response2.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertNotNull(response3.getHeaders().getFirst(HttpHeaders.SET_COOKIE));


    }

    @Test
    public void testLoginAndEmailUnique() throws JsonProcessingException {

        RegClientDtoRequest request1 = new RegClientDtoRequest("shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "+78005553535");
        RegClientDtoRequest request2 = new RegClientDtoRequest("shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        registerClient(request1);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_LOGIN.toString(), ErrorCode.INCORRECT_LOGIN.getField(), "User with login:shnayderdanila does exist"));

        try {
            registerClient(request2);
            fail();

        }
        catch (HttpClientErrorException e){
            ErrorDtoResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected.getErrors().size(), response.getErrors().size());
            assertEquals(expected.getErrors(), response.getErrors());
        }

    }

    @Test
    public void testIncorrectNumberAndEmail() throws JsonProcessingException {

        RegClientDtoRequest request1 = new RegClientDtoRequest("shnayderdanila1", "e365025", "Данила", "Шнайдер", "Владимирович", "shnaydermail.ru", "+88005553535");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("PhoneNumber", "phone", "Incorrect phone number:+88005553535"));
        expected.getErrors().add(new Error("Pattern", "email", "The field should contain @ and ."));

        try {
            registerClient(request1);
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
    public void testRegisterClientIncorrectData() throws JsonProcessingException {

        RegClientDtoRequest request = new RegClientDtoRequest("shnayderdan;dsa#2$%ila", "e36", "Danila", "Shnayder", "Vladimirovich", "shnayder@mail.ru", "+78005553535");

        ErrorDtoResponse expected = new ErrorDtoResponse();

        expected.getErrors().add(new Error("Pattern", "firstName", "The field can contain Russian letters"));
        expected.getErrors().add(new Error("StringMinSize","password","Your value:e36 is too short (less than 6 characters)"));
        expected.getErrors().add(new Error("StringMaxSize", "login", "Your value:shnayderdan;dsa#2$%ila is too large (more than 20 characters)"));
        expected.getErrors().add(new Error("Pattern", "login","The field can contain Russian and Latin letters of the alphabet, as well as numbers"));
        expected.getErrors().add(new Error("Pattern", "lastName","The field can contain Russian letters"));
        expected.getErrors().add(new Error("Pattern", "patronymic","The field can contain Russian letters"));

        try {
            registerClient(request);
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
    public void testUpdateClient(){

        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        String javaSessionId = registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "+78005553535", "e365025", "e123123");

        ResponseEntity<ClientDtoResponse> response = updateClient(javaSessionId, updateClientDtoRequest);

        ClientDtoResponse expected = new ClientDtoResponse(response.getBody().getId(), "Данила", "Шнайдер", "Владимирович",UserType.CLIENT.toString(), "shnayder@mail.ru", "+78005553535");

        assertEquals(expected, response.getBody());

    }

    @Test
    public void testUpdateClientIncorrectEmail() throws JsonProcessingException {

        RegClientDtoRequest requestRegisterClient1 = new RegClientDtoRequest(
                "shnayderdanila1", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        RegClientDtoRequest requestRegisterClient2 = new RegClientDtoRequest(
                "shnayderdanila2", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder1@mail.ru", "88005553535");

        registerClient(requestRegisterClient1).getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        String javaSessionId = registerClient(requestRegisterClient2).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "+78005553535", "e365025", "e123123");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_EMAIL.toString(), ErrorCode.INCORRECT_EMAIL.getField(), "User with email:shnayder@mail.ru doe's exist"));
        try {
            updateClient(javaSessionId, updateClientDtoRequest);
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));

        }

    }

    @Test
    public void testUpdateClientIncorrectData() throws JsonProcessingException {

        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        String javaSessionId = registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Danila", "Shnayder", "Vladimirovich", "shnaydermail.ru", "+88005553535", "e365025", "e36");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error("Pattern", "firstName", "The field can contain Russian letters"));
        expected.getErrors().add(new Error("StringMinSize","newPassword","Your value:e36 is too short (less than 6 characters)"));
        expected.getErrors().add(new Error("Pattern", "lastName","The field can contain Russian letters"));
        expected.getErrors().add(new Error("Pattern", "patronymic","The field can contain Russian letters"));
        expected.getErrors().add(new Error("Pattern", "email","The field should contain @ and ."));
        expected.getErrors().add(new Error("PhoneNumber", "phone","Incorrect phone number:+88005553535"));

        try {
            updateClient(javaSessionId, updateClientDtoRequest);
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
    public void testUpdateIncorrectPassword() throws JsonProcessingException {

        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        String javaSessionId = registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "+78005553535", "e123123", "e123123");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_PASSWORD.toString(), ErrorCode.WRONG_PASSWORD.getField(), ErrorCode.WRONG_PASSWORD.getMessage()));

        try {
            updateClient(javaSessionId, updateClientDtoRequest);
            fail();


        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }


    @Test
    public void testIncorrectJavaSessionId() throws JsonProcessingException {

        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "+78005553535", "e365025", "e123123");


        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));


        try {
            updateClient("", updateClientDtoRequest);
            fail();


        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }



    }

    @Test
    public void testExitClient() throws JsonProcessingException {


        RegClientDtoRequest requestRegisterClient = new RegClientDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "shnayder@mail.ru", "88005553535");

        String javaSessionId = registerClient(requestRegisterClient).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateClientDtoRequest updateClientDtoRequest = new UpdateClientDtoRequest("Данила", "Шнайдер", "Владимирович", "shnayder123@mail.ru", "+78005553535", "e365025", "e123123");

        exit(javaSessionId);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            updateClient(javaSessionId, updateClientDtoRequest);
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));

        }

    }

    @Test
    public void testNotBlank() {

        RegClientDtoRequest request = new RegClientDtoRequest(
                "     ", null, "", " ", "", "         ", "");

        UpdateClientDtoRequest updateAdminDtoRequest = new UpdateClientDtoRequest(" ", " ", " ", " ", null, " ","");


        assertThrows(HttpClientErrorException.class, () -> registerClient(request));
        assertThrows(HttpClientErrorException.class, () -> updateClient("", updateAdminDtoRequest));

    }


}
