package com.trilobyte.pelisdb.controllers.handler;

import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.trilobyte.pelisdb.dto.ErrorDto;
import com.trilobyte.pelisdb.exceptions.ApplicationException;
import com.trilobyte.pelisdb.exceptions.ValidationException;
import com.trilobyte.pelisdb.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
@Order(-10)
@Slf4j
public class MovieControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired(required = false)
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest webRequest) {
        return handleExceptionInternal(ex, body, headers, status, webRequest, false);
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest webRequest,
            final boolean audit) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            webRequest.setAttribute(
                    WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }

        final var resp = createError(status.value(), getPath(webRequest), getMethod(webRequest));
        String rawMsg = null;
        if (ex instanceof ValidationException) {
            rawMsg = toTranslateMessage((ValidationException) ex, messageSource);
        } else if (ex instanceof ApplicationException) {
            rawMsg =
                    MessageUtils.translateMessage(
                            messageSource, ((ApplicationException) ex).getExceptionMessage());
        } else if (body != null) {
            rawMsg = body.toString();
        } else if (ex != null) {
            rawMsg = ex.getMessage();
        }
        resp.setDescription(rawMsg);

        if (audit && log.isErrorEnabled()) {
            log.error(resp.toString(), ex);
        }

        return new ResponseEntity<>(resp, headers, status);
    }

    /* ********************************** */
    /* GENERAL */
    /* ********************************** */

    /**
     * Handles any other not handled exception in this class
     *
     * @param ex catched exception
     * @param request request which throws the exception
     * @return response REST
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> requestHandlingException(
            final Exception ex, final WebRequest request) {
        final var status = HttpStatus.INTERNAL_SERVER_ERROR;
        final var headers = new HttpHeaders();
        return handleExceptionInternal(ex, null, headers, status, request, true);
    }

    /**
     * Handles service itself exceptions
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> requestHandlingApplicationException(
            final ApplicationException ex, final WebRequest request) {
        final var status = HttpStatus.valueOf(ex.getCode());
        final var headers = new HttpHeaders();
        return handleExceptionInternal(ex, null, headers, status, request, false);
    }

    /* ********************************** */
    /* JSR 303 */
    /* ********************************** */

    /**
     * Handles JSR 303 Validation exceptions
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> requestHandlingConstraintViolationException(
            final ConstraintViolationException ex, final WebRequest request) {
        final var status = HttpStatus.BAD_REQUEST;
        final var headers = new HttpHeaders();
        final var newEx = toValidacionException(ex.getConstraintViolations());
        return handleExceptionInternal(newEx, null, headers, status, request, false);
    }

    /* ********************************** */
    /* DB */
    /* ********************************** */

    /**
     * Handles exceptions in an optimistic lock when validations fail (For example update a register
     * which field {@code @Version} has changes in other transaction)
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler({OptimisticLockException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<Object> requestHandlingOptimisticLockException(
            final Exception ex, final WebRequest request) {
        return requestDBException(ex, HttpStatus.CONFLICT, request);
    }

    /**
     * Handles non Spring encapsulated SQL exceptions
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> requestHandlingSQLException(
            final SQLException ex, final WebRequest request) {
        return requestDBException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles JTA transactions exceptions (Thrown after a commit or exiting the service, they can not
     * be catch in them)
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Object> requestHandlingTransactionException(
            final TransactionException ex, final WebRequest request) {
        return requestDBException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles Spring Data encapsulated exceptions
     *
     * @param ex Catched exception
     * @param request request which throws the exception
     * @return Response REST
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> requestHandlingDataAccessException(
            final DataAccessException ex, final WebRequest request) {
        return requestDBException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /* ********************************** */
    /* SPRING */
    /* ********************************** */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {
        final var result = ex.getBindingResult();
        final var newEx = toValidacionException(result.getAllErrors());
        return handleExceptionInternal(newEx, null, headers, status, request, false);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {
        if (ex.getCause() instanceof PropertyBindingException) {
            final var propEx = (PropertyBindingException) ex.getCause();
            final var ex2 = new ValidationException();
            final var msg =
                    new ApplicationException.ExceptionMessage(
                            "{error.handler.PropertyBindingException}", propEx.getKnownPropertyIds());
            msg.setFieldName(propEx.getPropertyName());
            ex2.addMessage(msg);
            return handleExceptionInternal(ex2, null, headers, status, request, true);
        }
        return handleExceptionInternal(ex, null, headers, status, request, true);
    }

    /* ********************************** */
    /* UTILS */
    /* ********************************** */

    /**
     * Handles JDBC (SQL or Transactions) exceptions
     *
     * @param ex exception to be handled
     * @param defaultStatus status code to be sent as a response if the most significant exception
     *     does not have any special treatment
     * @param request request which throws the exception
     * @return Response REST
     */
    protected ResponseEntity<Object> requestDBException(
            final Exception ex, final HttpStatus defaultStatus, final WebRequest request) {
        final var spec =
                ex instanceof NestedRuntimeException
                        ? ((NestedRuntimeException) ex).getMostSpecificCause()
                        : ex;
        if (spec instanceof ConstraintViolationException) {
            return requestHandlingConstraintViolationException(
                    (ConstraintViolationException) spec, request);
        }
        final var headers = new HttpHeaders();
        return handleExceptionInternal(
                spec instanceof Exception ? (Exception) spec : ex,
                null,
                headers,
                defaultStatus,
                request,
                true);
    }

    private static String getPath(final WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            return ((ServletWebRequest) webRequest).getRequest().getRequestURI();
        }
        return null;
    }

    private static String getMethod(final WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            return ((ServletWebRequest) webRequest).getRequest().getMethod();
        }
        return null;
    }

    /**
     * Converts a {@link ConstraintViolation}, {@link FieldError} or {@link ObjectError} list into a
     * {@link ValidationException}. Non extended the previous classes elements will not be added
     *
     * @param violations lista de {@link ConstraintViolation}, {@link FieldError} u {@link
     *     ObjectError}
     * @return {@link ValidationException}
     */
    private static ValidationException toValidacionException(final Collection<?> violations) {
        if (CollectionUtils.isEmpty(violations)) {
            return null;
        }

        final var ex2 = new ValidationException();
        for (final Object o : violations) {
            if (o instanceof ConstraintViolation) {
                final ConstraintViolation<?> t = (ConstraintViolation<?>) o;
                final var msg = new ApplicationException.ExceptionMessage(t.getMessage());
                msg.setFieldName(t.getPropertyPath() == null ? null : t.getPropertyPath().toString());
                ex2.addMessage(msg);
            } else if (o instanceof FieldError) {
                final var t = (FieldError) o;
                final var msg = new ApplicationException.ExceptionMessage(t.getDefaultMessage());
                msg.setFieldName(t.getField());
                ex2.addMessage(msg);
            } else if (o instanceof ObjectError) {
                final var t = (ObjectError) o;
                final var msg = new ApplicationException.ExceptionMessage(t.getDefaultMessage());
                ex2.addMessage(msg);
            }
        }

        return ex2;
    }

    private static ErrorDto createError(final int status, final String path, final String method) {
        final var err = new ErrorDto();
        err.setUuid(UUID.randomUUID());
        err.setStatus(status);
        err.setPath(path);
        err.setMethod(method);
        return err;
    }

    /**
     * Traduce si procede mensajes de error ${message}
     *
     * @param ex Excepción del cual se traducirán sus mensajes
     * @param messageSource Bundle de mensajes intercionalizados
     * @return String concatenado por {@code ,} de [Campo, Error traducido]
     */
    private static String toTranslateMessage(
            final ValidationException ex, final MessageSource messageSource) {
        final List<String> r = new ArrayList<>();
        for (final ApplicationException.ExceptionMessage msg : ex.getMessages()) {
            r.add(MessageUtils.translateMessage(messageSource, msg));
        }
        return String.join("\n", r);
    }
}
