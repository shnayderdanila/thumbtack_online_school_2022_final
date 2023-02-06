package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    private String fromDate;
    private String toDate;
    private String period;

}
