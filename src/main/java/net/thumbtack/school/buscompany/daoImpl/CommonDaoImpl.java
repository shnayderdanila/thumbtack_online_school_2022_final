package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.CommonDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.CommonMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@Slf4j
public class CommonDaoImpl implements CommonDao {

    private final CommonMapper commonMapper;

    public CommonDaoImpl(CommonMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    @Override
    public void clear() throws ServerException {
        log.debug("clear all database");
        try {
            commonMapper.clearUser();
            commonMapper.clearTrip();
        }
        catch (RuntimeException e){
            log.debug("ERROR:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "ERROR:"+ e);
        }
    }
}
