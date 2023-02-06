package net.thumbtack.school.buscompany.models;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class Client extends User{
    private String email;
    private String phone;

    @ToString.Exclude
    private List<Order> orders;

    public Client(int id, String login, String password, String firstName, String lastName, String patronymic, UserType userType, String email, String numberPhone) {
        super(id, login, password, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = numberPhone;
    }

}