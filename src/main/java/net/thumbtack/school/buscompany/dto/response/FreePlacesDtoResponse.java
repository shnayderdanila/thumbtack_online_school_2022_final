package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreePlacesDtoResponse {
    private Set<Integer> places;
}
