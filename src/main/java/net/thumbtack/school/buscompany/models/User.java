package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

    private int      id;
    private String   login;
    private String   password;
    private String   firstName;
    private String   lastName;
    private String   patronymic;
    private UserType userType;


}
