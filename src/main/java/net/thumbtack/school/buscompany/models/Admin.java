package net.thumbtack.school.buscompany.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User{
    private String position;

    public Admin(int id, String login, String password, String firstName, String lastName, String patronymic, UserType userType, String position) {
        super(id, login, password, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
