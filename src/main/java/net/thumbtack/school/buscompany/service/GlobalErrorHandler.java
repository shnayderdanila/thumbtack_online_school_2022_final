package net.thumbtack.school.buscompany.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.school.buscompany.dto.response.ErrorDtoResponse;
import net.thumbtack.school.buscompany.exception.Error;
import net.thumbtack.school.buscompany.exception.PathException;
import net.thumbtack.school.buscompany.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleValidation(MethodArgumentNotValidException exception){
        log.info("exception:{}", exception.getFieldErrors());
        final ErrorDtoResponse serverException = new ErrorDtoResponse();
        exception.getFieldErrors().forEach(fieldError -> {
                    log.info("Handle exception {}, {}, {}",fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage());
                    serverException.getErrors().add(new Error(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
                }
        );
        return serverException;
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleServerException(ServerException exception) {
        final ErrorDtoResponse serverException = new ErrorDtoResponse();
        log.info("Handle exception {}, {}, {}", exception.getErrorCode().toString(), exception.getErrorCode().getMessage(), exception.getErrorCode().getMessage());

        serverException.getErrors().add(new Error(exception.getErrorCode().toString(), exception.getErrorCode().getField(), exception.getErrorMessage()));
        return serverException;
    }

    @ExceptionHandler(PathException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDtoResponse handlePathException(PathException exception) {
        final ErrorDtoResponse serverException = new ErrorDtoResponse();
        log.info("Handle exception {}, {}, {}", exception.getErrorCode().toString(), exception.getErrorCode().getMessage(), exception.getErrorCode().getMessage());

        serverException.getErrors().add(new Error(exception.getErrorCode().toString(), exception.getErrorCode().getField(), exception.getErrorMessage()));
        return serverException;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDtoResponse handleException(BindException exception){
        log.info("exception:{}", exception.getFieldErrors());
        final ErrorDtoResponse serverException = new ErrorDtoResponse();
        exception.getFieldErrors().forEach(fieldError -> {
                    log.info("Handle exception {}, {}, {}",fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage());
                    serverException.getErrors().add(new Error(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
                }
        );
        return serverException;
    }

}
