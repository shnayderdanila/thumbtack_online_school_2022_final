package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.AdminMapper;
import net.thumbtack.school.buscompany.mappers.mybatis.UserMapper;
import net.thumbtack.school.buscompany.models.Admin;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@Slf4j
public class AdminDaoImpl implements AdminDao {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;

    public AdminDaoImpl(AdminMapper adminMapper, UserMapper userMapper) {
        this.adminMapper = adminMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public Admin insert(Admin administrator) throws ServerException {
       log.debug("DAO insert Admin {}", administrator);
        try {
            userMapper.insert(administrator);
            adminMapper.insert(administrator);
        }
        catch (RuntimeException e){
            log.info("Can't insert Admin {}", administrator, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't insert Admin:"+administrator);
        }

        return administrator;
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public void update(Admin administrator) throws  ServerException {
        log.debug("DAO update Admin:{}", administrator);
        try {
            userMapper.update(administrator);
            adminMapper.update(administrator);
        }
        catch (RuntimeException e){
            log.error("DAO can't update Admin. Errors:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't update Admin:"+administrator);
        }
    }

    @Override
    public Integer countAdmin() throws ServerException {
        log.debug("DAO count Admin");
        try {
            return adminMapper.count();
        }
        catch (RuntimeException e){
            log.error("Can't get count Admin. Error:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get count Admin");
        }
    }
}
