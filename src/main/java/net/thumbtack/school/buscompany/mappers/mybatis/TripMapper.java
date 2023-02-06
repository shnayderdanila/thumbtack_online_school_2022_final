package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.dto.request.GetTripParamsDtoRequest;
import net.thumbtack.school.buscompany.models.Bus;
import net.thumbtack.school.buscompany.models.Schedule;
import net.thumbtack.school.buscompany.models.Trip;
import net.thumbtack.school.buscompany.models.TripDate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TripMapper {

    //insert
    @Insert("INSERT INTO trip(id_bus, from_station, to_station, start, duration, price, approved) " +
            "VALUES(#{bus.id}, #{trip.fromStation}, #{trip.toStation}, #{trip.start}, #{trip.duration}, #{trip.price}, #{trip.approved})")
    @Options(useGeneratedKeys = true, keyProperty = "trip.id")
    Integer insertTrip(@Param("trip") Trip trip, @Param("bus") Bus bus);

    @Insert("<script>" +
            "INSERT INTO trip_date(id_trip, trip_date, free_places) VALUES" +
                "<foreach item ='item' collection ='dates' separator=','>" +
                    "(#{trip.id}, #{item.date}, #{trip.bus.placeCount})" +
                "</foreach>"+
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "dates.id")
    void batchInsertDate(@Param("trip") Trip trip, @Param("dates") List<TripDate> dates);

    @Insert("<script>" +
            "INSERT INTO places(id_trip_date, place, id_passenger) VALUES" +
                "<foreach item ='item' collection ='places' separator=','>" +
                    "(#{idTripDate}, #{item}, null)" +
                "</foreach>"+
            "</script>")
    void batchInsertPlace(@Param("idTripDate") int idTripDate, @Param("places") List<Integer> places);


    @Insert("INSERT INTO schedule(id_trip, from_date, to_date, period) " +
                            "VALUES(#{idTrip}, #{schedule.fromDate}, #{schedule.toDate}, #{schedule.period})")
    void insertSchedule(@Param("idTrip") int idTrip, @Param("schedule") Schedule schedule);

    //update
    @Update("UPDATE trip SET id_bus = #{bus.id}, " +
                            "from_station = #{update.fromStation}," +
                            "to_station = #{update.toStation}," +
                            "start = #{update.start}," +
                            "duration = #{update.duration}," +
                            "price = #{update.price} " +
            "WHERE id = #{update.id}")
    void updateTrip(@Param("update") Trip updateTrip, @Param("bus") Bus bus);

    @Update("UPDATE schedule SET from_date = #{schedule.fromDate}," +
                                "to_date = #{schedule.toDate}," +
                                "period = #{schedule.period}" +
            "WHERE id_trip = #{idTrip}")
    void updateSchedule(@Param("idTrip") int idTrip, @Param("schedule") Schedule schedule);

    @Update("UPDATE trip SET approved = #{trip.approved} WHERE id = #{trip.id}")
    void setApprovedTrip(@Param("trip") Trip trip);

    //delete

    @Delete("DELETE FROM trip WHERE trip.id = #{trip.id}")
    void deleteTrip(@Param("trip") Trip trip);

    @Delete("DELETE FROM trip_date WHERE id_trip = #{idTrip}")
    void deleteDates(@Param("idTrip") int idTrip);

    Trip getTripById(int idTrip);

    List<Trip> getTripsWithParam(GetTripParamsDtoRequest request);
}
