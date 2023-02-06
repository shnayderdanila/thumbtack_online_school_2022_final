package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDtoRequest {
    @NotBlank(message = "Please fill field")
    private String login;
    @NotBlank(message = "Please fill field")
    private String password;
}
