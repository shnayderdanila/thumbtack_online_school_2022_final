package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.request.SelectPlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.FreePlacesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SelectPlaceDtoResponse;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/places", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class PlacesEndPoint {

    private final OrderService orderService;

    public PlacesEndPoint(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{idOrder}")
    public FreePlacesDtoResponse getFreePlaces(@CookieValue("JAVASESSIONID") String javaSessionId,
                                               @PathVariable("idOrder") int idOrder) throws ServerException, PathException {
        log.debug("Get Free Places by:{} on Order with id:{}", javaSessionId, idOrder);
        return orderService.getFreePlaces(idOrder, javaSessionId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SelectPlaceDtoResponse selectPlace(@CookieValue("JAVASESSIONID") String javaSessionId,
                                              @RequestBody @Valid SelectPlaceDtoRequest request) throws ServerException, PathException {
        log.debug("Places end point select place by session:{} with request:{}", javaSessionId, request);
        return orderService.selectPlace(request, javaSessionId);
    }

}
