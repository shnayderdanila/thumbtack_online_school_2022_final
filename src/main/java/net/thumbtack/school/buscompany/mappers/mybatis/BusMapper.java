package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.Bus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusMapper {
    @Select("SELECT id, bus_name AS busName, place_count AS placeCount FROM bus")
    List<Bus> getAll();

    @Select("SELECT id, bus_name AS busName, place_count AS placeCount FROM bus WHERE bus_name = #{busName}")
    Bus getBusByName(@Param("busName") String busName);

}
