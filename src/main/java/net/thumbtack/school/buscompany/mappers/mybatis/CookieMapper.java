package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CookieMapper {

    @Insert("INSERT INTO cookie (id_user, java_session_id, time_leave) " +
            "VALUES(#{user.id}, #{javaSessionId}, #{date})")
    void login(@Param("user") User user, @Param("javaSessionId") String javaSessionId, @Param("date") String date);

    @Update("UPDATE cookie SET time_leave = #{time} WHERE java_session_id = #{javaSessionId}")
    void updateSession(@Param("javaSessionId") String javaSessionId, @Param("time") String time);

    @Delete("DELETE FROM cookie WHERE id_user = #{id}")
    void deleteJavaSessionById(@Param("id") int id);

    @Delete("DELETE FROM cookie WHERE #{now} > time_leave")
    void checkAndDeleteJavaSessionByTime(@Param("now") String now);


}
