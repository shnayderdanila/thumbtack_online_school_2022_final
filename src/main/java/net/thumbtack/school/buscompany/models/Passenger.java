package net.thumbtack.school.buscompany.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {

    @JsonIgnore
    private int    id;
    private String firstName;
    private String lastName;
    private String passport;
    @JsonIgnore
    private Integer place;

}
