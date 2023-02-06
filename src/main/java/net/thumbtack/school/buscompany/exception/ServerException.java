package net.thumbtack.school.buscompany.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServerException extends Exception{
    private ErrorCode errorCode;
    private String errorMessage;
}
