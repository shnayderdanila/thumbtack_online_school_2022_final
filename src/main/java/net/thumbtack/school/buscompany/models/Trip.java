package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {

    private int      id;
    private String   fromStation;
    private String   toStation;
    private String   start;
    private String   duration;
    private double   price;
    private Bus      bus;
    private boolean  approved;
    private Schedule schedule;

    @ToString.Exclude
    private List<TripDate> dates;


}
