package com.yunfeng.maven.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("logComponent")
public class LogComponent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void e(String message) {
        logger.error(message);
    }

    public void w(String message) {
        logger.warn(message);
    }
}
