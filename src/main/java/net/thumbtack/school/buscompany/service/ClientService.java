package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mapstruct.ClientConverter;
import net.thumbtack.school.buscompany.models.Client;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class ClientService extends BaseService{

    private final ClientDao clientDao;

    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public ResponseEntity<ClientDtoResponse> registerClient(RegClientDtoRequest request) throws ServerException {
        Client client = ClientConverter.MAPPER.regDtoToClient(request);

        log.info("Client service register Client:{}", client);
        if(!baseCheckLoginUsage(client.getLogin())){
            throw new ServerException(ErrorCode.INCORRECT_LOGIN, "User with login:"+client.getLogin()+" does exist");
        }
        if (clientDao.getClientIdByEmail(client.getEmail()) != null){
            throw new ServerException(ErrorCode.INCORRECT_EMAIL, "User with email:"+client.getEmail()+" does exist");
        }
        clientDao.insert(client);

        ClientDtoResponse response = ClientConverter.MAPPER.clientToDto(client);

        String javaSessionId = baseLogin(client);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie","JAVASESSIONID="+javaSessionId);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);

    }

    public ClientDtoResponse updateClient(UpdateClientDtoRequest update, String javaSessionId) throws ServerException {
        Client client = baseGetClientByJavaSessionId(javaSessionId);


        log.info("Client service update Client:{}, {}", client, update);
        if(!Pattern.matches(client.getPassword(), update.getOldPassword())){
            throw new ServerException(ErrorCode.WRONG_PASSWORD, ErrorCode.WRONG_PASSWORD.getMessage());
        }
        Integer idClientFromDB = clientDao.getClientIdByEmail(update.getEmail());
        if(idClientFromDB != null && idClientFromDB != client.getId()){
            throw new ServerException(ErrorCode.INCORRECT_EMAIL, "User with email:"+ update.getEmail()+" doe's exist");
        }

        ClientConverter.MAPPER.update(client, update);
        clientDao.update(client);
        return ClientConverter.MAPPER.clientToDto(client);
    }

    public Client getClientBySession(String javaSessionId) throws ServerException {
        log.info("Client Service get Client by session:{}", javaSessionId);

        return baseGetClientByJavaSessionId(javaSessionId);
    }
}
