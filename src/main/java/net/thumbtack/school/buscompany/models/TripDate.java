package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDate {

    private int    id;

    @ToString.Exclude
    private Trip   trip;
    private String date;

    @ToString.Exclude
    private List<Order> orders;

    public TripDate(int id, Trip trip, String date) {
        this.id   = id;
        this.trip = trip;
        this.date = date;
    }
}
