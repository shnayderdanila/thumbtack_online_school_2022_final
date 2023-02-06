package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.models.Passenger;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoResponse {

    private int    idOrder;
    private int    idTrip;
    private String fromStation;
    private String toStation;
    private String busName;
    private String date;
    private String start;
    private String duration;
    private double price;
    private double totalPrice;
    private List<Passenger> passengers;

}
