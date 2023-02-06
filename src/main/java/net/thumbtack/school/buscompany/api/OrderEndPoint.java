package net.thumbtack.school.buscompany.api;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.request.GetOrderParamsDtoRequest;
import net.thumbtack.school.buscompany.dto.request.RegOrderRequest;
import net.thumbtack.school.buscompany.dto.response.ListOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class OrderEndPoint {

    private final OrderService  orderService;

    public OrderEndPoint(OrderService orderService) {
        this.orderService   =  orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDtoResponse registerOrder(@CookieValue("JAVASESSIONID") String javaSessionId,
                                          @RequestBody @Valid RegOrderRequest request) throws ServerException, PathException {

        log.debug("Order end point register Order:{} by:{}", request, javaSessionId);
        return orderService.registerOrder(request, javaSessionId);

    }

    @GetMapping
    public ListOrderDtoResponse getOrdersWithParam(@CookieValue("JAVASESSIONID") String javaSessionId,
                                                   @Valid GetOrderParamsDtoRequest request) throws ServerException {
        log.debug("Order end point get Orders with param:{} by session:{}", request, javaSessionId);
        return orderService.getOrdersWithParam(request, javaSessionId);

    }

}
