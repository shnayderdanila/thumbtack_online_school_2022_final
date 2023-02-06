package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.date.Date;
import net.thumbtack.school.buscompany.validator.passport.Passport;

import javax.validation.constraints.Min;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegOrderRequest {

    @Min(1)   private int idTrip;
    @Date     private String date;
    @Passport private List<RegPassengerDto> passengers;

}
