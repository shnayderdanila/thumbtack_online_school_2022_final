package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.ClientMapper;
import net.thumbtack.school.buscompany.mappers.mybatis.UserMapper;
import net.thumbtack.school.buscompany.models.Client;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Service
@Slf4j
public class ClientDaoImpl implements ClientDao {
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;

    public ClientDaoImpl(ClientMapper clientMapper, UserMapper userMapper) {
        this.clientMapper = clientMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public Client insert(Client client) throws ServerException {
        log.debug("DAO insert Client {}", client);
        try {
            userMapper.insert(client);
            clientMapper.insert(client);
        }
        catch (RuntimeException e){
            log.error("Can't insert Client:{} Error:{}", client, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't insert Client:"+client);
        }

        return client;
    }

    @Override
    public List<Client> getAllClient() throws ServerException {
        log.debug("DAO get all client");
        try {
            return clientMapper.getAllClient();
        }
        catch (RuntimeException e){
            log.error("Can't get all Client. Error:{}", e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get all Client");
        }
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public void update(Client client) throws ServerException {
        log.debug("DAO update Client:{}", client);
        try {
            userMapper.update(client);
            clientMapper.update(client);
        }
        catch (RuntimeException e){
            log.error("Can't update Client:{}. Error:{}",client, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't update Client:"+client);
        }

    }

    @Override
    public Integer getClientIdByEmail(String email) throws ServerException {
        log.debug("DAO get client id by email {}", email);
        try {
            return clientMapper.getIdClientByEmail(email);
        }
        catch (RuntimeException e){
            log.error("Can't get client by email:{}. Error:{}", email, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't get client by email:"+email);
        }
    }
}
