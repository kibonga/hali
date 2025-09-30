package org.hali.logging;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.slf4j.LoggerFactory;

import java.util.Map;

@UtilityClass
@Slf4j
public class LogUtil {

    Logger logger = LogManager.getLogger(LogUtil.class);

    public void logInfo(String message, Map<String, String> fields) {
        final StringMapMessage mapMessage = new StringMapMessage();
        final var result = LoggerFactory.getILoggerFactory().getLogger(LogUtil.class.getName());
        result.info(message);

        mapMessage.put("message", message);
        mapMessage.putAll(fields);

        logger.info(mapMessage);
    }

    public void logInfo(String message) {
        final StringMapMessage mapMessage = new StringMapMessage();

        mapMessage.put("message", message);

        logger.info(mapMessage);
    }
}
