package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.BusDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.BusMapper;
import net.thumbtack.school.buscompany.models.Bus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@Slf4j
public class BusDaoImpl implements BusDao {

    private final BusMapper busMapper;

    public BusDaoImpl(BusMapper busMapper) {
        this.busMapper = busMapper;
    }

    @Override
    @Cacheable(cacheNames = "busses")
    public List<Bus> getAllBus() throws ServerException {
        log.debug("DAO get all bus");
        try {
            return busMapper.getAll();
        }
        catch (RuntimeException e){
            log.error("DAO can't get buses: {}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Cant get all buses");
        }
    }

    @Override
    @Cacheable(cacheNames = "busses")
    public Bus getBusByName(String busName) throws ServerException {
        log.debug("DAO get Bus by name:{}", busName);
        try {
            return busMapper.getBusByName(busName);
        }
        catch (RuntimeException e){
            log.error("DAO can't get Bus by name:{}", busName);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Cant get bus by name:"+busName);
        }

    }

}
