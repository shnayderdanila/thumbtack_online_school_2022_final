package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private int             id;
    private double          totalPrice;
    private List<Passenger> passengers;

    @ToString.Exclude
    private TripDate        tripDate;
}
