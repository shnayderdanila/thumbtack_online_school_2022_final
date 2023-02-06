package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectPlaceDtoRequest {
              private int    idOrder;
    @NotBlank private String lastName;
    @NotBlank private String firstName;
    @NotBlank private String passport;
    @Min(0)   private int    place;
}
