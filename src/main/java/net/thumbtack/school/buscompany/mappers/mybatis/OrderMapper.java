package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.dto.request.GetOrderParamsDtoRequest;
import net.thumbtack.school.buscompany.models.Order;
import net.thumbtack.school.buscompany.models.Passenger;
import net.thumbtack.school.buscompany.models.TripDate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (id_trip_date,    id_client,    total_price) " +
            "VALUES              (#{tripDate.id}, #{clientId}, #{order.totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "order.id")
    Integer insertOrder(@Param("clientId") int clientId,
                        @Param("order")    Order order,
                        @Param("tripDate") TripDate tripDate);

    @Insert("<script>"                                                                   +
                "INSERT INTO passenger(id_order, firstname, lastname, passport) "        +
                "VALUES"                                                                 +
                    "<foreach item ='passenger' collection ='passengers' separator=','>" +
                        "(#{order.id}, "                                                 +
                         "#{passenger.firstName}, "                                      +
                         "#{passenger.lastName}, "                                       +
                         "#{passenger.passport})"                                        +
                    "</foreach>"                                                         +
            "</script>")
    void batchInsertPassenger(@Param("order")       Order           order,
                              @Param("passengers")  List<Passenger> passengers);

    @Update("UPDATE trip_date "                                         +
            "SET "                                                      +
                   "free_places = free_places - #{countOrderedPlaces} " +
            "WHERE "                                                    +
                   "(free_places - #{countOrderedPlaces} > 0 OR "        +
                   "free_places - #{countOrderedPlaces} = 0) AND "       +
                   "id = #{tripDate.id}")
    Integer updateOrderedPlace(@Param("tripDate")           TripDate tripDate,
                               @Param("countOrderedPlaces") int      countOrderedPlaces);

    @Update("UPDATE places "                         +
            "SET "                                   +
                   "id_passenger = #{passenger.id} " +
            "WHERE  id_passenger IS NULL    AND "    +
                   "place        = #{place} AND "    +
                   "id_trip_date = #{tripDate.id}")
    Integer setPlacePassenger(@Param("tripDate")   TripDate  tripDate,
                              @Param("passenger")  Passenger passenger,
                              @Param("place")      int       place);

    @Select("SELECT free_places "  +
            "FROM   trip_date "                 +
            "WHERE  trip_date.id = #{tripDate.id}")
    Integer getCountFreePlaceOnTripDate(@Param("tripDate") TripDate tripDate);

    List<Order> getOrdersLazyByClient();

    List<Order> getOrdersWithParam(GetOrderParamsDtoRequest request);
}
