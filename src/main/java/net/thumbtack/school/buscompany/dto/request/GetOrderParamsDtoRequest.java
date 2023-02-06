package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.date.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderParamsDtoRequest {
          private String  fromStation;
          private String  toStation;
    @Date private String  fromDate;
    @Date private String  toDate;
          private String  busName;
          private Integer clientId;
}
