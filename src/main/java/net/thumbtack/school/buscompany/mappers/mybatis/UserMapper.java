package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (firstname, lastname, patronymic, login, password, user_type, deleted) " +
            "VALUES(#{user.firstName}, #{user.lastName}, #{user.patronymic}, #{user.login}, #{user.password}, #{user.userType}, false)")
    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    Integer insert(@Param("user") User user);

    @Update("UPDATE user SET firstname = #{user.firstName}, lastname = #{user.lastName}, patronymic = #{user.patronymic}," +
            "password = #{user.password} WHERE id = #{user.id} AND deleted = false")
    void update(@Param("user") User user);

    @Update("UPDATE user SET deleted = true WHERE id = #{id}")
    void deleteById(@Param("id") int id);

    @Select("SELECT login FROM user WHERE login = #{login}")
    String getLoginByLogin(@Param("login") String login);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") int id);

    User getUserByJavaSessionId(@Param("javaSessionId") String javaSessionId);
}
