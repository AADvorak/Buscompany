package net.thumbtack.school.buscompany.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(BusAppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusAppException(BusAppException exc) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error(exc.getErrorCode().name(), exc.getErrorCode().getErrorDescription().getField(),
                exc.getErrorCode().getErrorDescription().getDescription()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException exc) {
        final ErrorResponse error = new ErrorResponse();
        exc.getBindingResult().getFieldErrors().forEach(fieldError-> {
            error.getErrors().add(new Error(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
        });
        exc.getBindingResult().getGlobalErrors().forEach(err-> {
            error.getErrors().add(new Error(err.getCode(), err.getObjectName(), err.getDefaultMessage()));
        });
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        final ErrorResponse error = new ErrorResponse();
        exc.getBindingResult().getFieldErrors().forEach(fieldError-> {
            error.getErrors().add(new Error(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
        });
        exc.getBindingResult().getGlobalErrors().forEach(err-> {
            error.getErrors().add(new Error(err.getCode(), err.getObjectName(), err.getDefaultMessage()));
        });
        return error;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("INVALID_MEDIA_TYPE", null, e.getMessage()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ErrorResponse handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("INVALID_MEDIA_TYPE", null, e.getMessage()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("HTTP_MSG_NOT_READABLE", null, e.getMessage()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("METHOD_ARGUMENT_TYPE_MISMATCH", null, e.getMessage()));
        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("NOT_FOUND", e.getRequestURL() + "," + e.getHttpMethod(), e.getMessage()));
        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        final ErrorResponse error = new ErrorResponse();
        error.getErrors().add(new Error("INTERNAL_SERVER_ERROR", null, e.getMessage()));
        return error;
    }

}

