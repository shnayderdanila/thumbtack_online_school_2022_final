package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.dto.request.GetOrderParamsDtoRequest;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.models.Order;
import net.thumbtack.school.buscompany.models.Passenger;
import net.thumbtack.school.buscompany.models.TripDate;

import java.util.List;
import java.util.Set;

public interface OrderDao {

    boolean register(int clientId, Order order) throws ServerException;

    boolean updatePlacePassenger(Order order, Passenger passenger, int place) throws ServerException;

    Set<Integer> getFreePlacesOnTripDate(TripDate tripDate) throws ServerException;

    Integer getCountFreePlaceOnTripDate(TripDate tripDate) throws ServerException;

    List<Order> getOrdersWithParam(GetOrderParamsDtoRequest request) throws ServerException;

}
