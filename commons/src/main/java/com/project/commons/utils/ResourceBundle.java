package com.project.commons.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Created by user on 16:21 18/05/2025, 2025
 */
public class ResourceBundle {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String messageKey, Locale locale, Object... args) {
        return messageSource.getMessage(messageKey, args, locale);
    }

    public String getMessage(String messageKey, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(messageKey, locale, args);
    }
}
