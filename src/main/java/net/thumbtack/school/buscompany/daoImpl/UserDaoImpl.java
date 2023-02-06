package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.CookieMapper;
import net.thumbtack.school.buscompany.mappers.mybatis.UserMapper;
import net.thumbtack.school.buscompany.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@Slf4j
public class UserDaoImpl implements UserDao {

    private final UserMapper userMapper;
    private final CookieMapper cookieMapper;

    public UserDaoImpl(UserMapper userMapper, CookieMapper cookieMapper) {
        this.userMapper = userMapper;
        this.cookieMapper = cookieMapper;
    }

    @Override
    public void login(User user, String javaSessionId, String date) throws ServerException {
        log.debug("DAO login {}, {}, {}", user, javaSessionId, date);
        try {
            cookieMapper.login(user, javaSessionId, date);
        }
        catch (RuntimeException e){
            log.error("DAO can't login User:{}, {}, {}", user, javaSessionId, date);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't login User:"+user.getId());
        }
    }

    @Override
    public User getUserByLogin(String login) throws ServerException {
        log.debug("DAO get user by login {}", login);
        try {
            return userMapper.getUserByLogin(login);
        }
        catch (RuntimeException e){
            log.error("Can't get User by login:{}. Error:{}",login, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get User by login:"+login);
        }
    }

    @Override
    public User getUserByJavaSessionId(String javaSessionId) throws ServerException {
        log.debug("DAO get User by java session id {}", javaSessionId);
        try {
            return userMapper.getUserByJavaSessionId(javaSessionId);
        }
        catch (RuntimeException e){
            log.error("Can't get User by java session id:{}. Error:{}",javaSessionId, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get User by java session id:"+javaSessionId);
        }
    }

    @Override
    public void updateSessionId(String javaSessionId, String time) throws ServerException {
        log.debug("DAO update session:{}", javaSessionId);
        try {
            cookieMapper.updateSession(javaSessionId, time);
        }
        catch (RuntimeException e){
            log.error("Can't update session:{}. Error:{}",javaSessionId, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't update session:{}"+javaSessionId);

        }
    }

    @Override
    public void deleteById(int id) throws ServerException {
        log.debug("DAO delete user by id {}", id);
        try {
            userMapper.deleteById(id);
        }
        catch (RuntimeException e){
            log.error("DAO can't delete user by id:{}. Error:{}", id, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't delete user by id:"+id);
        }
    }


    @Override
    public String getLoginByLogin(String login) throws ServerException {
        log.debug("DAO get login by login:{}", login);
        try {
            return userMapper.getLoginByLogin(login);

        }
        catch (RuntimeException e){
            log.error("Can't get login:{}. Error:{}",login, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get login by login:"+login);
        }

    }

}
