package com.project.auth.commons;

/**
 * Created by user on 6:29 06/07/2025, 2025
 */

import com.project.commons.utils.MessageUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtilInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final MessageSource messageSource;

    public MessageUtilInitializer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        MessageUtil.setMessageSource(messageSource);
    }
}
