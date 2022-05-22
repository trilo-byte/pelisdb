package com.trilobyte.pelisdb.exceptions;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/** Exception thrown by a component of the application itself */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 2054925292100329723L;

    protected static final int DEFAULT_CODE = 500;

    private final ExceptionMessage msg;

    /**
     * Constructs a new runtime exception with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    protected ApplicationException() {
        super();
        msg = null;
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     */
    public ApplicationException(final String message) {
        this.msg = new ExceptionMessage(message);
    }

    /**
     * Construct the exception with the message
     *
     * @param message message with validation error
     * @param params Optional. If the message is a key of a properties (it must go between braces
     *     {...}, its parameters can be provided (value of {0}, {1} ...)
     */
    public ApplicationException(final String message, final Object... params) {
        this.msg = new ExceptionMessage(message, params);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *     (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     *     unknown.)
     */
    public ApplicationException(final String message, final Throwable cause) {
        super(cause);
        this.msg = new ExceptionMessage(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *     (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     *     unknown.)
     */
    public ApplicationException(final ExceptionMessage message, final Throwable cause) {
        super(cause);
        this.msg = message;
    }

    public ExceptionMessage getExceptionMessage() {
        return msg;
    }

    @Override
    public String getMessage() {
        return msg.toString();
    }

    /**
     * Retrieves internal error code
     *
     * @return internal error code
     */
    public int getCode() {
        return DEFAULT_CODE;
    }

    /**
     * Message contained in the exception {@link ValidacionException}
     *
     */
    @lombok.Getter
    @lombok.Setter
    public static class ExceptionMessage implements Serializable {

        private static final long serialVersionUID = -4397288750704935820L;

        /** Optional. ID field that has launched the error */
        private String fieldName;

        /**
         * Text of the message. You can go between braces {...} if you want to retrieve the message from
         * a properties
         */
        private final String text;
        /**
         * If the message is a key of a properties (it must go between braces {...}, you can provide its
         * parameters (value of {0}, {1} ...)
         */
        private final transient Object[] params;

        /**
         * Constructor
         *
         * @param text Text of the message. You can go between braces {...} if you want to retrieve the
         *     message from a properties
         * @param params If the message is a key of a properties (it must go between braces {...}, you
         *     can provide its parameters (value of {0}, {1} ...)
         */
        public ExceptionMessage(final String text, final Object... params) {
            this.text = text;
            this.params = params;
        }

        @Override
        public String toString() {
            if (StringUtils.hasText(fieldName)) {
                return new StringBuilder(fieldName).append(": ").append(text).toString();
            }
            return text;
        }
    }

}
