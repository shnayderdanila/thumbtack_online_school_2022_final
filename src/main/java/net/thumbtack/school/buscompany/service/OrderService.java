package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.OrderDao;
import net.thumbtack.school.buscompany.dto.request.GetOrderParamsDtoRequest;
import net.thumbtack.school.buscompany.dto.request.RegOrderRequest;
import net.thumbtack.school.buscompany.dto.request.SelectPlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.response.FreePlacesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ListOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SelectPlaceDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mapstruct.OrderConverter;
import net.thumbtack.school.buscompany.models.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService extends BaseService{

    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public OrderDtoResponse registerOrder(RegOrderRequest request, String javaSessionId) throws ServerException, PathException {

        Client client = baseGetClientByJavaSessionId(javaSessionId);
        Trip   trip   = baseGetTripById(request.getIdTrip());

        Order  order  = OrderConverter.MAPPER.dtoToOrder(request);

        log.info("Order service register Order:{} on Trip:{}", order, trip);

        TripDate tripDate = checkCorrectTripAndGetTripDate(request.getDate(), trip);

        order.setTripDate(tripDate);

        order.setTotalPrice(
                tripDate.getTrip().getPrice() * order.getPassengers().size()
        );

        if( checkFreePlacesOnTrip(order.getTripDate()) ||
            !orderDao.register(client.getId(), order) )
        {

            throw new ServerException(ErrorCode.ALL_PLACE_BUSY, ErrorCode.ALL_PLACE_BUSY.getMessage());

        }

        return OrderConverter.MAPPER.orderToDto( order );

    }

    public ListOrderDtoResponse getOrdersWithParam(GetOrderParamsDtoRequest request, String javaSessionId) throws ServerException {
        log.info("Order service get Orders with params:{}", request);

        User user = baseGetUserByJavaSessionId(javaSessionId);

        if (user.getUserType() == UserType.CLIENT) {
            request.setClientId(user.getId());
        }

        return new ListOrderDtoResponse(
                                        orderDao .getOrdersWithParam(request)
                                                 .stream()
                                                 .map(OrderConverter.MAPPER::orderToDto)
                                                 .collect(Collectors.toList())
                                        );

    }

    public SelectPlaceDtoResponse selectPlace(SelectPlaceDtoRequest request, String javaSessionId) throws ServerException, PathException {
        log.debug("Order service select place on Order with id:{}", request.getIdOrder());
        Client client = baseGetClientByJavaSessionId(javaSessionId);
        Order  order  = getOrderByClient(request.getIdOrder(), client);

        log.info("Order service select place on Order:{} by Client:{}", order, client);

        //не совсем понимаю как убрать эту проверку в кеш, потому что в запросе есть только idOrder, нужно сперва достать order, потом достать tripDate,
        //потом достать Trip и только потом Bus, а без БД не понимаю как проверить
        if(request.getPlace() > order.getTripDate().getTrip().getBus().getPlaceCount()){
            throw new ServerException(ErrorCode.INCORRECT_PLACE, ErrorCode.INCORRECT_PLACE.getMessage());
        }

        Passenger passenger =  order .getPassengers()
                                     .stream()
                                     .filter(x -> x.getPassport().equals(request.getPassport()))
                                     .findFirst()
                                     .orElseThrow
                                             (()-> new ServerException(ErrorCode.INCORRECT_PASSENGER,
                                                     "Passenger with passport:" + request.getPassport()+
                                                             " does not exist in order:"+ order.getId()));

        if(  orderDao.getFreePlacesOnTripDate(order.getTripDate()).contains(request.getPlace()) &&
            !orderDao.updatePlacePassenger(order, passenger, request.getPlace()) )
        {

            throw new ServerException(ErrorCode.BUSY_PLACE, "Place:"+request.getPlace()+" is busy.");

        }

        String ticket = "Билет " + order.getTripDate().getTrip().getId()+"_"+request.getPlace();

        return new SelectPlaceDtoResponse(request.getIdOrder(),  ticket,
                                          request.getLastName(), request.getFirstName(),
                                          request.getPassport(), request.getPlace());

    }

    public FreePlacesDtoResponse getFreePlaces(int idOrder, String javaSessionId) throws ServerException, PathException {
        Client client = baseGetClientByJavaSessionId(javaSessionId);
        Order  order  = getOrderByClient(idOrder, client);

        log.info("Get Free Places by Client:{} on Order with id:{}", client, order);

        return new FreePlacesDtoResponse(orderDao.getFreePlacesOnTripDate(order.getTripDate()));

    }

    private Order getOrderByClient(int idOrder, Client client) throws PathException, ServerException {

        if(client.getOrders() == null){
            throw new ServerException(ErrorCode.INCORRECT_ORDER, "You don't have orders");
        }

        for(Order order : client.getOrders()){
            if(order.getId() == idOrder){
                return order;
            }
        }

        throw new PathException(ErrorCode.INCORRECT_ORDER, ErrorCode.INCORRECT_ORDER.getMessage());
    }

    private boolean checkFreePlacesOnTrip(TripDate tripDate) throws ServerException {
        return orderDao.getCountFreePlaceOnTripDate(tripDate) == 0;
    }


    private TripDate checkCorrectTripAndGetTripDate(String tripDate, Trip trip) throws ServerException {
        if(!trip.isApproved()){
            throw new ServerException(ErrorCode.WRONG_ACTION, "You can not register on un approved Trip:"+trip.getId());
        }
        return trip .getDates()
                    .stream()
                    .filter(date -> date.getDate().equals(tripDate)).findFirst()
                    .orElseThrow(() -> new ServerException(ErrorCode.INCORRECT_ORDER_DATE, ErrorCode.INCORRECT_ORDER_DATE.getMessage()));

    }

}
