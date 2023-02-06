package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.response.ListBusDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/buses" )
@Slf4j
public class BusEndPoint {

    private final AdminService adminService;

    public BusEndPoint(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ListBusDtoResponse getBuses(@CookieValue("JAVASESSIONID") String javaSessionID) throws ServerException {
        log.debug("Bus controller get all buses by session:{}", javaSessionID);
        return adminService.getBuses(javaSessionID);
    }

}
