package net.thumbtack.school.buscompany.dto.response;

import lombok.Data;
import net.thumbtack.school.buscompany.exception.Error;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorDtoResponse {
    private List<Error> errors;

    public ErrorDtoResponse() {
        this.errors = new ArrayList<>();
    }
}
