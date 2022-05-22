package com.trilobyte.pelisdb.exceptions;

public class MovieNotFoundException extends ApplicationException {

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     */
    public MovieNotFoundException(final String message) {
        super(message);
    }

    /**
     * Construct the exception with the message
     *
     * @param message message with validation error
     * @param params Optional. If the message is a key of a properties (it must go between braces
     *     {...}, its parameters can be provided (value of {0}, {1} ...)
     */
    public MovieNotFoundException(final String message, final Object... params) {
        super(message, params);
    }

    @Override
    public int getCode() {
        return 404;
    }

}
