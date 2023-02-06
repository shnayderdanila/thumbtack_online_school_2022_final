package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BusDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dto.request.RegAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListBusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListClientDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mapstruct.AdminConverter;
import net.thumbtack.school.buscompany.mappers.mapstruct.BusConverter;
import net.thumbtack.school.buscompany.mappers.mapstruct.ClientConverter;
import net.thumbtack.school.buscompany.models.Admin;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class AdminService extends BaseService{

    private final AdminDao  adminDao;
    private final ClientDao clientDao;
    private final BusDao    busDao;

    public AdminService(AdminDao adminDao, ClientDao clientDao, BusDao busDao) {
        this.adminDao  = adminDao;
        this.clientDao = clientDao;
        this.busDao    = busDao;
    }

    public ResponseEntity<AdminDtoResponse> registerAdmin(RegAdminDtoRequest request) throws ServerException {

        Admin admin = AdminConverter.MAPPER.RegDtoToAdmin(request);

        if(!baseCheckLoginUsage(admin.getLogin())){
            throw new ServerException(ErrorCode.INCORRECT_LOGIN, "User with login:"+admin.getLogin()+" does exist");
        }

        AdminDtoResponse response = AdminConverter.MAPPER.adminToRegResponse(adminDao.insert(admin));

        String javaSessionId = baseLogin(admin);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie","JAVASESSIONID="+javaSessionId);

        log.debug("Admin service register Admin {}", admin);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);

    }

    public AdminDtoResponse updateAdmin(UpdateAdminDtoRequest update, String javaSessionId) throws ServerException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        log.debug("Admin service update Admin:{}, {}",admin, update);

        if(!Pattern.matches(admin.getPassword(), update.getOldPassword())){
            throw new ServerException(ErrorCode.WRONG_PASSWORD, ErrorCode.WRONG_PASSWORD.getMessage());
        }

        AdminConverter.MAPPER.update(admin, update);

        adminDao.update(admin);

        return AdminConverter.MAPPER.adminToRegResponse(admin);

    }

    public ListBusDtoResponse getBuses(String javaSessionId) throws ServerException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        log.info("Admin service get buses by Admin:{}", admin);
        return new ListBusDtoResponse(
                BusConverter.MAPPER.listBusToListResp(busDao.getAllBus())
        );

    }

    public ListClientDtoResponse getAllClient(String javaSessionId) throws ServerException {

        Admin admin = baseGetAdminByJavaSessionId(javaSessionId);
        log.debug("Admin service get all Client by Admin:{}", admin);

        return new ListClientDtoResponse(
                ClientConverter.MAPPER.listClientToListDto(clientDao.getAllClient())
        );

    }

    public Admin getAdminBySession(String javaSessionId) throws ServerException {

        log.debug("Admin service get Admin by session:{}", javaSessionId);
        return baseGetAdminByJavaSessionId(javaSessionId);

    }

}
