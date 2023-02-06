package net.thumbtack.school.buscompany.mappers.mapstruct;

import net.thumbtack.school.buscompany.dto.request.RegClientDtoRequest;
import net.thumbtack.school.buscompany.dto.request.UpdateClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientConverter {
    ClientConverter MAPPER = Mappers.getMapper(ClientConverter.class);

    @Mappings({
            @Mapping(target = "id",         ignore = true),
            @Mapping(target = "userType",   constant = "CLIENT")
    })
    Client regDtoToClient(RegClientDtoRequest request);

    @Mappings({
            @Mapping(target = "id",         ignore = true),
            @Mapping(target = "login",      ignore = true),
            @Mapping(target = "password",   source = "newPassword"),
            @Mapping(target = "userType",   ignore = true)
    })
    void update(@MappingTarget Client client, UpdateClientDtoRequest update);

    ClientDtoResponse clientToDto(Client client);

    List<ClientDtoResponse> listClientToListDto(List<Client> list);
}
