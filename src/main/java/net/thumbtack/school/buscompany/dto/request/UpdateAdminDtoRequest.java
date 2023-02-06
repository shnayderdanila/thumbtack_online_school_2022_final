package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.size.StringMaxSize;
import net.thumbtack.school.buscompany.validator.size.StringMinSize;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminDtoRequest {

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    @Pattern(regexp = "[а-яА-Я ]+", message = "The field can contain Russian letters")
    private String firstName;

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    @Pattern(regexp = "[а-яА-Я ]+", message = "The field can contain Russian letters")
    private String lastName;

    @StringMaxSize
    @Pattern(regexp = "[а-яА-Я ]+", message = "The field can contain Russian letters")
    private String patronymic;

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    private String position;

    @NotBlank(message = "Please fill field")
    private String oldPassword;

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    @StringMinSize
    private String newPassword;

}
