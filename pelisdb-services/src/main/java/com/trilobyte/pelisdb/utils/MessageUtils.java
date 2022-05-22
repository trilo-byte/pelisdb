package com.trilobyte.pelisdb.utils;

import com.trilobyte.pelisdb.exceptions.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Slf4j
public class MessageUtils {

    private MessageUtils() {
        // private constructor
    }

    /**
     * Traduce (si procede) un mensaje que hace referencia a un property de mensajes (el mensaje va
     * entre llaves {...})
     *
     * @param messageSource Fuente de mensajes de la aplicación
     * @param message Objeto que contiene la información del mensaje
     * @return mensaje final
     */
    public static String translateMessage(
            final MessageSource messageSource, final ApplicationException.ExceptionMessage message) {
        return translateMessage(LocaleContextHolder.getLocale(), messageSource, message);
    }

    /**
     * Traduce (si procede) un mensaje que hace referencia a un propery de mensajes (el mensaje va
     * entre llaves {...})
     *
     * @param messageSource Fuente de mensajes de la aplicación
     * @param locale Locale del mensaje
     * @param message Objeto que contiene la información del mensaje
     * @return mensaje final
     */
    public static String translateMessage(
            final Locale locale, final MessageSource messageSource, final ApplicationException.ExceptionMessage message) {
        final var msg = translateMessage(locale, messageSource, message.getText(), message.getParams());
        final var label = message.getFieldName();
        if (StringUtils.hasText(label)) {
            return new StringBuilder(label).append(": ").append(msg).toString();
        }
        return msg;
    }

    public static String translateMessage(
            final MessageSource messageSource, final String msg, final Object... params) {
        return translateMessage(LocaleContextHolder.getLocale(), messageSource, msg, params);
    }

    public static String translateMessage(
            final Locale locale,
            final MessageSource messageSource,
            final String msg,
            final Object... params) {
        if (messageSource != null
                && StringUtils.hasText(msg)
                && msg.startsWith("{")
                && msg.endsWith("}")) {
            final var key = msg.substring(1, msg.length() - 1);
            try {
                return messageSource.getMessage(key, params, locale);
            } catch (final NoSuchMessageException ignore) {
                log.warn(String.format("There is no translation for: {0}", messageSource));
            }
        }
        return msg;
    }
}
