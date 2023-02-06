package net.thumbtack.school.buscompany.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.models.Bus;
import net.thumbtack.school.buscompany.models.Schedule;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDtoResponse {

    private int idTrip;
    private String fromStation;
    private String toStation;
    private String start;
    private String duration;
    private double price;
    private Bus bus;
    private boolean approved;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Schedule schedule;
    private List<String> dates;

}
