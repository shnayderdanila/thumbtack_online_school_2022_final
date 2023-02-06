package net.thumbtack.school.buscompany.mappers.mapstruct;

import net.thumbtack.school.buscompany.dto.request.RegAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.models.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminConverter {
    AdminConverter MAPPER = Mappers.getMapper(AdminConverter.class);

    @Mappings({
            @Mapping(target = "id",         ignore = true),
            @Mapping(target = "userType",   constant = "ADMIN")
    })
    Admin RegDtoToAdmin(RegAdminDtoRequest request);

    AdminDtoResponse adminToRegResponse(Admin administrator);

    @Mappings({
            @Mapping(target = "id",         ignore = true),
            @Mapping(target = "login",      ignore = true),
            @Mapping(target = "password",   source = "newPassword"),
            @Mapping(target = "userType",   ignore = true)
    })
    void update(@MappingTarget Admin admin, UpdateAdminDtoRequest update);
}
