package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.CookieDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.CookieMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@Slf4j
public class CookieDaoImpl implements CookieDao {
    private final CookieMapper cookieMapper;

    public CookieDaoImpl(CookieMapper cookieMapper) {
        this.cookieMapper = cookieMapper;
    }

    @Override
    public void checkAndDeleteJavaSessionByTime(String now) throws ServerException {
        //log.debug("DAO check and delete java session id by time:"+now);
        try {
            cookieMapper.checkAndDeleteJavaSessionByTime(now);
        }
        catch (RuntimeException e){
            log.error("DAO can't check and delete java session by time:{}. Error:{}",now, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't check and delete java session by time:"+now);
        }
    }

    @Override
    public void deleteJavaSessionById(int id) throws ServerException {
        //  log.debug("DAO delete java session by id {}", id);
        try {
            cookieMapper.deleteJavaSessionById(id);
        }
        catch (RuntimeException e){
            log.error("DAO can't delete java session by id:{}. Error:{}", id, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't delete java session by id:"+id);
        }
    }

}
