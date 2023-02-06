package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.configuration.ValidationProperties;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.CookieDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.EmptyDto;
import net.thumbtack.school.buscompany.dto.request.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.SettingsAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mapstruct.AdminConverter;
import net.thumbtack.school.buscompany.mappers.mapstruct.ClientConverter;
import net.thumbtack.school.buscompany.models.Admin;
import net.thumbtack.school.buscompany.models.Client;
import net.thumbtack.school.buscompany.models.User;
import net.thumbtack.school.buscompany.models.UserType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService extends BaseService{

    private final UserDao   userDao;
    private final AdminDao  adminDao;
    private final CookieDao cookieDao;

    private final ValidationProperties properties;

    public UserService(UserDao userDao, AdminDao adminDao, CookieDao cookieDao, ValidationProperties properties) {
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.cookieDao = cookieDao;
        this.properties = properties;
    }

    public String login(User user) throws ServerException {
        log.debug("User service login {}", user);

        return baseLogin(user);
    }

    public ResponseEntity<UserDtoResponse> authorization(LoginDtoRequest request) throws ServerException {

        User user = userDao.getUserByLogin(request.getLogin());

        if(user == null){
            throw new ServerException(ErrorCode.INCORRECT_LOGIN, "User " + request.getLogin() + " doesn't exist");
        }

        if(!Pattern.matches(user.getPassword(), request.getPassword())){
            throw new ServerException(ErrorCode.WRONG_PASSWORD, ErrorCode.WRONG_PASSWORD.getMessage());
        }

        log.debug("User service login User:{}", user);
        String javaSessionId = baseLogin(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie","JAVASESSIONID="+javaSessionId);

        UserDtoResponse response;
        if(user.getUserType() == UserType.ADMIN){
            response = AdminConverter.MAPPER.adminToRegResponse((Admin) user);
        }
        else {
            response = ClientConverter.MAPPER.clientToDto((Client) user);
        }

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
    }

    public EmptyDto logout(String javaSessionId) throws ServerException {

        User user = baseGetUserByJavaSessionId(javaSessionId);
        log.info("User service logout User:{}", user);

        cookieDao.deleteJavaSessionById(user.getId());

        return new EmptyDto();
    }

    public EmptyDto deleteUser(String javaSessionId) throws ServerException {
        User user = baseGetUserByJavaSessionId(javaSessionId);
        log.info("User Service delete User:{}", user);
        if(user.getUserType() == UserType.ADMIN && adminDao.countAdmin() == 1){
            throw new ServerException(ErrorCode.INCORRECT_ADMIN_ACTION, ErrorCode.INCORRECT_ADMIN_ACTION.getMessage());
        }

        cookieDao.deleteJavaSessionById(user.getId());

        userDao.deleteById(user.getId());

        return new EmptyDto();
    }

    public UserDtoResponse getUserBySession(String javaSessionId) throws ServerException {
        User user = baseGetUserByJavaSessionId(javaSessionId);
        log.info("Get User:{} by session:{}", user, javaSessionId);
        if(user.getUserType() == UserType.ADMIN){
            return AdminConverter.MAPPER.adminToRegResponse((Admin) user);
        }
        else {
            return ClientConverter.MAPPER.clientToDto((Client) user);
        }
    }

    public SettingsDtoResponse getSettings(String javaSessionId) throws ServerException {
        User user = baseGetUserByJavaSessionId(javaSessionId);

        if(user.getUserType() == UserType.ADMIN){
            return new SettingsAdminDtoResponse(properties.getMax_name_length(),  properties.getMin_password_length());
        }
        else {
            return new SettingsClientDtoResponse(properties.getMax_name_length(), properties.getMin_password_length());
        }
    }

}
