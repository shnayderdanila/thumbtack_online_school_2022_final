package net.thumbtack.school.buscompany.mappers.mybatis;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {


    @Delete("DELETE FROM user")
    void clearUser();

    @Delete("DELETE FROM trip")
    void clearTrip();

}
