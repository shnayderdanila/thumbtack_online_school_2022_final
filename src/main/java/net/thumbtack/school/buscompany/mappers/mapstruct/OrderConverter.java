package net.thumbtack.school.buscompany.mappers.mapstruct;

import net.thumbtack.school.buscompany.dto.request.RegOrderRequest;
import net.thumbtack.school.buscompany.dto.response.OrderDtoResponse;
import net.thumbtack.school.buscompany.models.Order;
import net.thumbtack.school.buscompany.models.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderConverter {
    OrderConverter MAPPER = Mappers.getMapper(OrderConverter.class);

    default Order dtoToOrder(RegOrderRequest request){
        Order order = new Order();

        List<Passenger> passengers = new ArrayList<>();

        request.getPassengers().forEach(x ->
                passengers.add(new Passenger(0, x.getFirstName(), x.getLastName(), x.getPassport(), null)));
        order.setPassengers(passengers);
        return order;
    }

    default OrderDtoResponse orderToDto(Order order){
        OrderDtoResponse response = new OrderDtoResponse();

        response.setIdOrder     ( order.getId() );
        response.setDate        ( order.getTripDate().getDate() );
        response.setTotalPrice  ( order.getTotalPrice() );
        response.setPassengers  ( order.getPassengers() );

        response.setIdTrip      ( order.getTripDate().getTrip().getId() );
        response.setFromStation ( order.getTripDate().getTrip().getFromStation() );
        response.setToStation   ( order.getTripDate().getTrip().getToStation() );
        response.setBusName     ( order.getTripDate().getTrip().getBus().getBusName() );
        response.setStart       ( order.getTripDate().getTrip().getStart() );
        response.setDuration    ( order.getTripDate().getTrip().getDuration() );
        response.setPrice       ( order.getTripDate().getTrip().getPrice() );

        return response;
    }
}
