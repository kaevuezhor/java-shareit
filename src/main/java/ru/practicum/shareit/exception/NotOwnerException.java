package ru.practicum.shareit.exception;

public class NotOwnerException extends Exception {
    public NotOwnerException(String message) {
        super(message);
    }
}
