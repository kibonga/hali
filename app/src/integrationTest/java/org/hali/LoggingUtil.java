package org.hali;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

@UtilityClass
public class LoggingUtil {
    public static void overrideLog4j2Appenders(String url) {
        final ConfigurationBuilder<BuiltConfiguration> configurationBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

        final AppenderComponentBuilder httpAppender = configurationBuilder
            .newAppender("Integration - HTTP", "Http")
            .addAttribute("url", url)
            .add(configurationBuilder.newLayout("JsonLayout").addAttribute("compact", true));

        final AppenderComponentBuilder consoleAppender = configurationBuilder
            .newAppender("Integration - Console", "Console")
            .add(configurationBuilder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} KIBONGA:- %msg%n"));

        final AppenderComponentBuilder fileAppender = configurationBuilder
            .newAppender("Integration - File", "File")
            .addAttribute("fileName", "kibonga-appender.log")
            .add(configurationBuilder.newLayout("JsonLayout").addAttribute("compact", true));

        final Configuration configuration = configurationBuilder
            .add(httpAppender)
            .add(consoleAppender)
            .add(fileAppender)
            .add(
                configurationBuilder.newRootLogger(Level.INFO)
                    .add(configurationBuilder.newAppenderRef("Integration - HTTP"))
                    .add(configurationBuilder.newAppenderRef("Integration - Console"))
                    .add(configurationBuilder.newAppenderRef("Integration - File"))
            )
            .build(false);

        final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.start(configuration);
    }
}
