package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.Client;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClientMapper {

    @Insert("INSERT INTO client (id_user, phone, email) VALUES(#{client.id}, #{client.phone}, #{client.email})")
    void insert(@Param("client") Client client);

    @Update("UPDATE client SET phone = #{client.phone}, email = #{client.email} WHERE id_user = #{client.id}")
    void update(@Param("client") Client client);

    @Select("SELECT id_user FROM client JOIN user ON user.id = id_user WHERE email = #{email} AND deleted = false")
    Integer getIdClientByEmail(@Param("email") String email);

    List<Client> getAllClient();


}
