package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.models.Schedule;
import net.thumbtack.school.buscompany.validator.trip.Trip;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Trip
public class TripDtoRequest {

    @NotBlank(message = "Please fill field")
    private String busName;

    @NotBlank(message = "Please fill field")
    private String fromStation;

    @NotBlank(message = "Please fill field")
    private String toStation;

    @NotBlank(message = "Please fill field")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String start;

    @NotBlank(message = "Please fill field")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
    private String duration;

    @Min(value = 1, message = "Price should be more then 1")
    private double price;

    private Schedule schedule;

    private List<String> dates;

}
