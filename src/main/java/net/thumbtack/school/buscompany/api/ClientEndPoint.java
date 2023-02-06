package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ClientEndPoint {

    private final ClientService clientService;

    public ClientEndPoint(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientDtoResponse> registerClient(@RequestBody @Valid RegClientDtoRequest request) throws ServerException {

        log.debug("Client controller register user {}", request);
        return clientService.registerClient(request);

    }

    @PutMapping("/clients")
    public ClientDtoResponse updateClient(@RequestBody @Valid UpdateClientDtoRequest request,
                                          @CookieValue("JAVASESSIONID") String javaSessionId) throws ServerException {

        log.debug("Client controller update Client:{}, {}",javaSessionId, request);
        return clientService.updateClient(request, javaSessionId);

    }

}
