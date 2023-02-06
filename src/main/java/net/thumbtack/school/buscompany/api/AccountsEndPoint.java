package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountsEndPoint {

    private final UserService userService;

    public AccountsEndPoint(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    public EmptyDto deleteUser(@CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Exit User:{}", javaSessionId);
        return userService.deleteUser(javaSessionId);

    }

    @GetMapping
    public UserDtoResponse getUserBySession(@CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Get user by session:{}", javaSessionId);
        return userService.getUserBySession(javaSessionId);

    }



}

