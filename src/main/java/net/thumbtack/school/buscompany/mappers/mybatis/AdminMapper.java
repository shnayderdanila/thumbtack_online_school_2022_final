package net.thumbtack.school.buscompany.mappers.mybatis;

import net.thumbtack.school.buscompany.models.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {

    @Insert("INSERT INTO admin (id_user, position) VALUES(#{admin.id}, #{admin.position})")
    void insert(@Param("admin") Admin admin);

    @Select("SELECT COUNT(*) FROM user WHERE deleted = false AND user_type = 'ADMIN'")
    Integer count();

    @Update("UPDATE admin SET position = #{admin.position} WHERE id_user = #{admin.id}")
    void update(@Param("admin") Admin admin);
}
