package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.TripDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface TripDateMapper {

    @Select("SELECT place " +
                "FROM   places " +
                "JOIN   trip_date " +
                    "ON trip_date.id = id_trip_date " +
                "WHERE  id_passenger IS NULL AND " +
                       "id_trip_date = #{tripDate.id}")
    Set<Integer> getFreePlacesOnTripDate(int id);

    TripDate getTripDateById(int id);

    List<TripDate> getTripsDateByTrip(int idTrip);
}
