package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOrderDtoResponse {

    private List<OrderDtoResponse> list;

}
