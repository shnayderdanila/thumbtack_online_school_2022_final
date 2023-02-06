package net.thumbtack.school.buscompany.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bus {
    private int     id;
    private String  busName;
    private int     placeCount;
}
