package com.project.client.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Created by user on 8:42 18/05/2025, 2025
 */
@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:/messages"   // for common directory with messages_XX.properties "classpath:/auth/messages"
        );
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setAlwaysUseMessageFormat(true);
        return messageSource;
    }

    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(List.of(new Locale("en"), new Locale("id")));
        resolver.setDefaultLocale(Locale.ENGLISH); // fallback
        return resolver;
    }

}
