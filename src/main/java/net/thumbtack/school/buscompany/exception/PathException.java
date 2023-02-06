package net.thumbtack.school.buscompany.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PathException extends Exception{
    private ErrorCode errorCode;
    private String errorMessage;
}