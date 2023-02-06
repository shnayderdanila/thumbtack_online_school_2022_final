package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.request.RegAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListClientDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AdminEndPoint {

    private final AdminService adminService;

    public AdminEndPoint(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admins")
    public ResponseEntity<AdminDtoResponse> registerAdmin(@RequestBody @Valid RegAdminDtoRequest request) throws ServerException {
        log.debug("Admin end point register Admin:{}", request);
        return adminService.registerAdmin(request);
    }

    @PutMapping("/admins")
    public AdminDtoResponse updateAdmin(@RequestBody @Valid UpdateAdminDtoRequest request,
                                        @CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Admin end point update Admin:{}, {}",javaSessionId, request);
        return adminService.updateAdmin(request, javaSessionId);

    }

    @GetMapping(value = "/clients", consumes = MediaType.ALL_VALUE)
    public ListClientDtoResponse getAllClient(@CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Client controller get all client by admin session: {}", javaSessionId);
        return adminService.getAllClient(javaSessionId);
    }


}
