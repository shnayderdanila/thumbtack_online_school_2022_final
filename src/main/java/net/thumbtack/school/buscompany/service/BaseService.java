package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.CookieDao;
import net.thumbtack.school.buscompany.dao.TripDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.*;
import net.thumbtack.school.buscompany.utils.SimpleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@Slf4j
public class BaseService {

    @Autowired private UserDao   userDao;
    @Autowired private CookieDao cookieDao;
    @Autowired private TripDao   tripDao;

    @Value("${user_idle_timeout}") private int user_idle_timeout;

    protected String baseLogin(User user) throws ServerException {
        log.info("Base service baseLogin for User:{}", user);
        cookieDao.deleteJavaSessionById(user.getId());

        log.info("Create session user {}", user);

        String javaSessionId = UUID.randomUUID().toString().replace("-", "");

        userDao.login(user,
                javaSessionId,
                SimpleFormatter.dateWithTimeFormatter.format(
                        LocalDateTime.now(ZoneId.of("GMT")).plusMinutes(user_idle_timeout)));

        return javaSessionId;
    }

    protected boolean baseCheckLoginUsage(String login) throws ServerException {
        log.info("Base service check login usage:{}", login);
        String ans = userDao.getLoginByLogin(login);
        if(ans == null){
            log.info("Login {} is valid", login);
            return true;
        }
        log.info("Login {} isn't valid", login);
        return false;
    }

    protected User baseGetUserByJavaSessionId(String javaSessionId) throws ServerException {
        log.info("Base service get User by java session id:{}", javaSessionId);
        User user = userDao.getUserByJavaSessionId(javaSessionId);

        if(user == null){
            throw new ServerException(ErrorCode.INCORRECT_JAVA_SESSION_ID, ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage());
        }

        updateSession(javaSessionId);

        return user;
    }

    protected Client baseGetClientByJavaSessionId(String javaSessionId) throws ServerException {
        log.info("Base service get Client by java session id:{}", javaSessionId);
        User client = userDao.getUserByJavaSessionId(javaSessionId);

        if(client == null || client.getUserType() != UserType.CLIENT){
            throw new ServerException(ErrorCode.INCORRECT_JAVA_SESSION_ID, ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage());
        }

        updateSession(javaSessionId);

        return (Client) client;
    }


    protected Admin baseGetAdminByJavaSessionId(String javaSessionId) throws ServerException {
        log.info("Base service get Admin by java session id:{}", javaSessionId);
        User admin = userDao.getUserByJavaSessionId(javaSessionId);
        
        if(admin == null || admin.getUserType() != UserType.ADMIN){
            throw new ServerException(ErrorCode.INCORRECT_JAVA_SESSION_ID, ErrorCode.INCORRECT_JAVA_SESSION_ID.getMessage());
        }

        updateSession(javaSessionId);

        return (Admin) admin;
    }

    protected Trip baseGetTripById(int idTrip) throws PathException, ServerException {
        log.info("Base service get Trip by id:{}", idTrip);

        Trip trip = tripDao.getTripById(idTrip);

        if( trip == null ){
            throw new PathException(ErrorCode.INCORRECT_ID_TRIP, "Incorrect id Trip:"+idTrip);
        }

        return trip;
    }

    private void updateSession(String javaSessionId) throws ServerException {
        userDao.updateSessionId(javaSessionId,
                SimpleFormatter.dateWithTimeFormatter.format(
                        LocalDateTime.now(ZoneId.of("GMT")).plusMinutes(user_idle_timeout)));

    }
}
