package com.trilobyte.pelisdb.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends ApplicationException {

    private static final long serialVersionUID = 1467552028286090050L;

    private final List<ExceptionMessage> messages = new ArrayList<>();

    /** Constructor */
    public ValidationException() {
        super();
    }

    /**
     * Constructor
     *
     * @param valError message information to insert
     */
    public ValidationException(final ExceptionMessage valError) {
        super();
        this.addMessage(valError);
    }

    /**
     * Construct the exception with the first message
     *
     * @param message message with validation error
     * @param params Optional. If the message is a key of a properties (it must go between braces
     *     {...}, its parameters can be provided (value of {0}, {1} ...)
     */
    public ValidationException(final String message, final Object... params) {
        super();
        this.addMessage(message, params);
    }

    /**
     * Checks if the exception contains error messages
     *
     * @return {@code false} if the exception contains error messages
     */
    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    /**
     * Add an error message
     *
     * @param message message to add
     * @param params Optional. If the message is a key of a properties (it must go between braces
     *     {...}, its parameters can be provided (value of {0}, {1} ...)
     */
    public void addMessage(final String message, final Object... params) {
        this.messages.add(new ExceptionMessage(message, params));
    }

    /**
     * Add an error message
     *
     * @param message message to add
     */
    public void addMessage(final ExceptionMessage message) {
        this.messages.add(message);
    }

    @Override
    public String getMessage() {
        return this.messages.stream().map(ExceptionMessage::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public int getCode() {
        return 400;
    }
}
