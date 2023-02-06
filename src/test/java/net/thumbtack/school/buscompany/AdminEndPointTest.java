package net.thumbtack.school.buscompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.school.buscompany.dto.request.RegAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListClientDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.models.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AdminEndPointTest extends BaseTest{

    @Test
    public void testRegisterAdmin(){

        RegAdminDtoRequest request = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        ResponseEntity<AdminDtoResponse> adminDtoResponse = registerAdmin(request);



        AdminDtoResponse expected = new AdminDtoResponse(
                Objects.requireNonNull(adminDtoResponse.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.ADMIN.toString(), "admin");
        assertEquals(expected, adminDtoResponse.getBody());
        assertNotNull(adminDtoResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

    }

    @Test
    public void testUpdateAdmin(){

        RegAdminDtoRequest registerAdmin = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        String javaSessionId = registerAdmin(registerAdmin).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest("Данила", "Шнайдер", "Владимирович", "boss", "e365025", "e123123");

        ResponseEntity<AdminDtoResponse> response = updateAdmin(javaSessionId, updateAdminDtoRequest);


        AdminDtoResponse expected = new AdminDtoResponse(
                Objects.requireNonNull(response.getBody()).getId(), "Данила", "Шнайдер", "Владимирович", UserType.ADMIN.toString(), "boss");

        assertEquals(expected, response.getBody());

    }


    @Test
    public void testExitLastAdmin() throws JsonProcessingException {


        RegAdminDtoRequest registerAdminRequest1 = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        RegAdminDtoRequest registerAdminRequest2 = new RegAdminDtoRequest(
                "shnayderdanila1", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");


        String javaSessionId1 = registerAdmin(registerAdminRequest1).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        String javaSessionId2 = registerAdmin(registerAdminRequest2).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        exit(javaSessionId1);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_ADMIN_ACTION.toString(), ErrorCode.INCORRECT_ADMIN_ACTION.getField(), ErrorCode.INCORRECT_ADMIN_ACTION.getMessage()));

        try {
            exit(javaSessionId2);
            fail();

        }
        catch (HttpClientErrorException e){
            ErrorDtoResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, response);
        }


    }

    @Test
    public void testLoginUnique() throws JsonProcessingException {

        RegAdminDtoRequest registerAdminRequest1 = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        RegAdminDtoRequest registerAdminRequest2 = new RegAdminDtoRequest(
                "shnayderdanila1", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");


        String javaSessionId = registerAdmin(registerAdminRequest1).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        registerAdmin(registerAdminRequest2).getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        exit(javaSessionId);

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_LOGIN.toString(),
                                           ErrorCode.INCORRECT_LOGIN.getField(),
                                    "User with login:shnayderdanila does exist"));

        try {
            registerAdmin(registerAdminRequest1);
            fail();

        }
        catch (HttpClientErrorException e){
            ErrorDtoResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, response);
        }


    }

    @Test
    public void testIncorrectRegisterData() throws JsonProcessingException {

        RegAdminDtoRequest errorRequest1 = new RegAdminDtoRequest(
                "shnayderdan;dsa#2$%ila", "e36", "Danila", "Shnayder", "Vladimirovich", "admin");

        ErrorDtoResponse expected = new ErrorDtoResponse();

        expected.getErrors().add(new Error("Pattern", "firstName", "The field can contain Russian letters"));
        expected.getErrors().add(new Error("StringMinSize","password","Your value:e36 is too short (less than 6 characters)"));
        expected.getErrors().add(new Error("StringMaxSize", "login", "Your value:shnayderdan;dsa#2$%ila is too large (more than 20 characters)"));
        expected.getErrors().add(new Error("Pattern", "login","The field can contain Russian and Latin letters of the alphabet, as well as numbers"));
        expected.getErrors().add(new Error("Pattern", "lastName","The field can contain Russian letters"));
        expected.getErrors().add(new Error("Pattern", "patronymic","The field can contain Russian letters"));

        try {
            registerAdmin(errorRequest1);
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

        RegAdminDtoRequest registerAdmin = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        String javaSessionId = registerAdmin(registerAdmin).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest("Данила", "Шнайдер", "Владимирович", "boss", "e123123", "e123123");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.WRONG_PASSWORD.toString(), ErrorCode.WRONG_PASSWORD.getField(), ErrorCode.WRONG_PASSWORD.getMessage()));


        try {
            updateAdmin(javaSessionId, updateAdminDtoRequest);
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }

    }


    @Test
    public void testIncorrectUpdateData() throws JsonProcessingException {

        RegAdminDtoRequest registerAdmin = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        String javaSessionId = registerAdmin(registerAdmin).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest("Danila", "Shnayder", "Vladimirovich", "boss", "e365025", "e36");

        ErrorDtoResponse expected = new ErrorDtoResponse();

        expected.getErrors().add(new Error("Pattern", "firstName", "The field can contain Russian letters"));
        expected.getErrors().add(new Error("StringMinSize","newPassword","Your value:e36 is too short (less than 6 characters)"));
        expected.getErrors().add(new Error("Pattern", "lastName","The field can contain Russian letters"));
        expected.getErrors().add(new Error("Pattern", "patronymic","The field can contain Russian letters"));

        try {
            updateAdmin(javaSessionId, updateAdminDtoRequest);
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
    public void testIncorrectJavaSessionId() throws JsonProcessingException {

        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest("Данила", "Шнайдер", "Владимирович", "boss", "e365025", "e123123");

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));


        try {
            System.out.println(
                    updateAdmin("", updateAdminDtoRequest)
            );
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));
        }



    }

    @Test
    public void testNotBlank() {

        RegAdminDtoRequest request = new RegAdminDtoRequest(
                "     ", null, "", " ", "", "         ");

        UpdateAdminDtoRequest updateAdminDtoRequest = new UpdateAdminDtoRequest(" ", " ", " ", " ", null, " ");


        assertThrows(HttpClientErrorException.class, () -> registerAdmin(request));
        assertThrows(HttpClientErrorException.class, () -> updateAdmin("", updateAdminDtoRequest));

    }

    @Test
    public void testGetAllClient(){


        RegAdminDtoRequest registerAdmin = new RegAdminDtoRequest(
                "shnayderdanila", "e365025", "Данила", "Шнайдер", "Владимирович", "admin");

        RegClientDtoRequest request1 = new RegClientDtoRequest(
                "client1", "e365025", "Данила", "Шнайдер", "Владимирович", "client1@mail.ru", "88005553535");

        RegClientDtoRequest request2 = new RegClientDtoRequest(
                "client2", "e365025", "Данила", "Шнайдер", "Владимирович", "client2@mail.ru", "88005553535");

        RegClientDtoRequest request3 = new RegClientDtoRequest(
                "client3", "e365025", "Данила", "Шнайдер", "Владимирович", "client3@mail.ru", "88005553535");

        String javaSessionIdAdmin = registerAdmin(registerAdmin).getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        ResponseEntity<ClientDtoResponse> client1 = registerClient(request1);
        ResponseEntity<ClientDtoResponse> client2 = registerClient(request2);
        ResponseEntity<ClientDtoResponse> client3 = registerClient(request3);

        String javaSessionIdClient2 = client2.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];
        String javaSessionIdClient3 = client3.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        ListClientDtoResponse expected1 = new ListClientDtoResponse(new ArrayList<>());
        expected1.getList().add(new ClientDtoResponse(client1.getBody().getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "client1@mail.ru", "88005553535"));
        expected1.getList().add(new ClientDtoResponse(client2.getBody().getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "client2@mail.ru", "88005553535"));
        expected1.getList().add(new ClientDtoResponse(client3.getBody().getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "client3@mail.ru", "88005553535"));

        ListClientDtoResponse expected2 = new ListClientDtoResponse(new ArrayList<>());
        expected2.getList().add(new ClientDtoResponse(client1.getBody().getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "client1@mail.ru", "88005553535"));
        expected2.getList().add(new ClientDtoResponse(client2.getBody().getId(), "Данила", "Шнайдер", "Владимирович", UserType.CLIENT.toString(), "client2@mail.ru", "88005553535"));

        ResponseEntity<ListClientDtoResponse> response1 = getAllClient(javaSessionIdAdmin);

        logout(javaSessionIdClient2);
        exit(javaSessionIdClient3);

        ResponseEntity<ListClientDtoResponse> response2 = getAllClient(javaSessionIdAdmin);

        assertEquals(expected1.getList().size(), response1.getBody().getList().size());
        assertTrue(expected1.getList().containsAll(response1.getBody().getList()));

        assertEquals(expected2.getList().size(), response2.getBody().getList().size());
        assertTrue(expected2.getList().containsAll(response2.getBody().getList()));


    }

    @Test
    public void testAllClientWrongJavaSessionId() throws JsonProcessingException {

        RegClientDtoRequest request1 = new RegClientDtoRequest(
                "client1", "e365025", "Данила", "Шнайдер", "Владимирович", "client1@mail.ru", "88005553535");
        ResponseEntity<ClientDtoResponse> client1 = registerClient(request1);
        String javaSessionIdClient = client1.getHeaders().getFirst(HttpHeaders.SET_COOKIE).split("=")[1];

        ErrorDtoResponse expected = new ErrorDtoResponse();
        expected.getErrors().add(new Error(ErrorCode.INCORRECT_JAVA_SESSION_ID.toString(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getField(), ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage()));

        try {
            getAllClient(javaSessionIdClient);
            fail();

        }
        catch (HttpClientErrorException e){

            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(expected, objectMapper.readValue(e.getResponseBodyAsString(), ErrorDtoResponse.class));

        }


    }

}
