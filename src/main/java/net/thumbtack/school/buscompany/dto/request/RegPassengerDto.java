package net.thumbtack.school.buscompany.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegPassengerDto {

    private String firstName;
    private String lastName;
    private String passport;

}
