package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectPlaceDtoResponse {
    private int idOrder;
    private String ticket;
    private String lastName;
    private String firstName;
    private String passport;
    private int place;
}
