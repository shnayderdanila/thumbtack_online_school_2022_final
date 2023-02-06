package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDtoResponse extends UserDtoResponse implements Serializable {
    private int    id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
    private String email;
    private String phone;

}
