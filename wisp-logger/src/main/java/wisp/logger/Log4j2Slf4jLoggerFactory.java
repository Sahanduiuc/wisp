/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wisp.logger;

import com.typesafe.config.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Implementation of SLF4J's LoggerFactory which binds in a configured log4j2 logging layer
 * and provides a simple default configuration if none provided.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class Log4j2Slf4jLoggerFactory implements Slf4jLoggerFactory {
    @Override
    public void configure(Config config) {
        if (config.hasPath("logging.config")) {
            String logConfigPath = config.getString("logging.config");
            InputStream in = getClass().getResourceAsStream(logConfigPath);
            if (in == null) {
                try {
                    in = new FileInputStream(new File(logConfigPath));
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("bad logConfig: " + logConfigPath);
                }
            }

            ConfigurationSource source;
            try {
                source = new ConfigurationSource(in);
            } catch (IOException e) {
                throw new IllegalStateException("unable to init log4j2 from " + logConfigPath, e);
            }
            Configurator.initialize(null, source);
        } else {
            initDefaultLogConfig();
        }
    }

    @Override
    public void start() {
        initDefaultLogConfig();
        getLogger(getClass()).info("initialized logging layer");
    }

    @Override
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }


    private static void initDefaultLogConfig() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
            ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d %highlight{%p} %C{1.} [%t] %style{%m}{bold,green}%n"));
        builder.add(appenderBuilder);
        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Stdout")));

        Configurator.initialize(builder.build());
    }
}
