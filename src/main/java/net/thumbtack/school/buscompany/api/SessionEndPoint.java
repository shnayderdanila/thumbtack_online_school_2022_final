package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.request.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class SessionEndPoint {

    private final UserService userService;

    public SessionEndPoint(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDtoResponse> login(@RequestBody @Valid LoginDtoRequest request) throws ServerException {
        log.debug("Session end point login User by request:{}", request.getLogin());
        return userService.authorization(request);
    }

    @DeleteMapping
    public EmptyDto logout(@CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Session end point logout by java session id:{}", javaSessionId);
        return userService.logout(javaSessionId);
    }

}
