package ru.practicum.shareit.exception;

public class EmailsConflictException extends RuntimeException {
    public EmailsConflictException(String message) {
        super(message);
    }
}
