package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.model.ErrorResponse;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAlreadyExistsException(final AlreadyExistsException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleAccessException(final AccessException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotOwnerException(final NotOwnerException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleUnavailableException(final UnavailableException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleAlreadyApprovedException(final AlreadyApprovedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleNotBookedException(final NotBookedException e) {
        return new ErrorResponse(e.getMessage());
    }
}
