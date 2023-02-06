package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.size.StringMaxSize;
import net.thumbtack.school.buscompany.validator.size.StringMinSize;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegAdminDtoRequest implements Serializable {

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    @Pattern(regexp = "[1-9a-zA-Zа-яА-Я]+", message = "The field can contain Russian and Latin letters of the alphabet, as well as numbers")
    private String login;

    @NotBlank(message = "Please fill field")
    @StringMaxSize
    @StringMinSize
    private String password;

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
}
