package net.thumbtack.school.buscompany.mappers.mapstruct;

import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
import net.thumbtack.school.buscompany.models.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusConverter {
    BusConverter MAPPER = Mappers.getMapper(BusConverter.class);

    List<BusDtoResponse> listBusToListResp(List<Bus> list);
}
