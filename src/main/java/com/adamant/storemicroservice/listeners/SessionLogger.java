package com.adamant.storemicroservice.listeners;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionLogger implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionLogger.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.info("Session created: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.info("Session destroyed (timeout or logout): " + se.getSession().getId());
    }
}
