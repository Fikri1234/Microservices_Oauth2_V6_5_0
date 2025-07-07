package com.project.commons.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Created by user on 16:21 18/05/2025, 2025
 */

public class MessageUtil {


    private static MessageSource messageSource;

    // Static initializer (called from config)
    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    public static String getMessage(String key, Object[] args, Locale locale) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized");
        }
        return messageSource.getMessage(key, args, locale);
    }

    public static String getMessage(String key, Locale locale) {
        return getMessage(key, null, locale);
    }

    public static String getMessage(String key) {
        return getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
