package net.thumbtack.school.buscompany.daoImpl;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dao.OrderDao;
import net.thumbtack.school.buscompany.dto.request.GetOrderParamsDtoRequest;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.mappers.mybatis.OrderMapper;
import net.thumbtack.school.buscompany.mappers.mybatis.TripDateMapper;
import net.thumbtack.school.buscompany.models.Order;
import net.thumbtack.school.buscompany.models.Passenger;
import net.thumbtack.school.buscompany.models.TripDate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Service
@Slf4j
public class OrderDaoImpl implements OrderDao {

    private final OrderMapper    orderMapper;
    private final TripDateMapper tripDateMapper;

    public OrderDaoImpl(OrderMapper orderMapper, TripDateMapper tripDateMapper) {
        this.orderMapper    = orderMapper;
        this.tripDateMapper = tripDateMapper;
    }

    @Override
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED)
    public boolean register(int clientId, Order order) throws ServerException {
        log.debug("DAO register Order:{}", order);
        try {
            if(orderMapper.updateOrderedPlace(order.getTripDate(),order.getPassengers().size()) == 0){
                return  false;
            }
            orderMapper.insertOrder(clientId, order, order.getTripDate());
            orderMapper.batchInsertPassenger(order, order.getPassengers());
            return true;
        }
        catch (RuntimeException e){
            log.error("Can't register Order:{} by ClientId:{}. Error:{}",order, clientId, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't register Order:"+order+" by ClientId:"+clientId);
        }
    }

    @Override
    public boolean updatePlacePassenger(Order order, Passenger passenger, int place) throws ServerException {
        log.debug("DAO update place Passenger");
        try {
            return orderMapper.setPlacePassenger(order.getTripDate(), passenger, place) != 0;
        }
        catch (RuntimeException e){
            log.error("DAO can't update place Passenger in Order:{}. Error:{}", order, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can't update place Passenger in Order:"+order);
        }
    }


    @Override
    public Set<Integer> getFreePlacesOnTripDate(TripDate tripDate) throws ServerException {
        log.debug("DAO get free places on date:{}", tripDate);
        try {
            Set<Integer> response = tripDateMapper.getFreePlacesOnTripDate(tripDate.getId());
            if(response.isEmpty()){
                response = new HashSet<>(List.of(0));
            }
            return response;
        }
        catch (RuntimeException e){
            log.error("Error get free places on date:{}. Error:{}",tripDate, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can not get free places on trip date:"+tripDate);
        }
    }

    @Override
    public Integer getCountFreePlaceOnTripDate(TripDate tripDate) throws ServerException {
        log.debug("DAO get count free place on TripDate:{}", tripDate);

        try {
            return orderMapper.getCountFreePlaceOnTripDate(tripDate);
        }
        catch (RuntimeException e){
            log.error("Error get count free place on TripDate:{}. Error:{}",tripDate, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Can not get count free place on TripDate:"+tripDate);
        }
    }

    @Override
    public List<Order> getOrdersWithParam(GetOrderParamsDtoRequest request) throws ServerException {
        try {
            return orderMapper.getOrdersWithParam(request);
        }
        catch (RuntimeException e){
            log.error("Can't get Orders with param:{}. Error:{}",request, e);
            throw new ServerException(ErrorCode.DATABASE_EXCEPTION, "Cant get Orders with param:"+request);
        }
    }

}
