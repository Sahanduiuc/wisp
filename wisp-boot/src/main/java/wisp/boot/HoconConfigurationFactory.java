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

package wisp.boot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import wisp.api.Configuration;
import wisp.api.ConfigurationFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Period;

/**
 * Thin wrapper around the TypeSafe Config library to handle parsing HOCON files.
 * This factory handles any config file ending with *.conf.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class HoconConfigurationFactory implements ConfigurationFactory {
    @Override
    public boolean canHandle(Path sourceFilePath) {
        return sourceFilePath.endsWith(".conf");
    }

    @Override
    public Configuration parse(Path sourceFilePath) {
        Config config = ConfigFactory.parseFileAnySyntax(sourceFilePath.toFile());
        return new HoconConfiguration(config);
    }

    HoconConfiguration create(Config config) {
        return new HoconConfiguration(config);
    }

    private class HoconConfiguration implements Configuration {
        private final Config config;

        private HoconConfiguration(Config config) {
            this.config = config;
        }

        @Override
        public boolean hasPath(String path) {
            return config.hasPath(path);
        }

        @Override
        public boolean hasPathOrNull(String path) {
            return config.hasPathOrNull(path);
        }

        @Override
        public boolean getBoolean(String path) {
            return config.getBoolean(path);
        }

        @Override
        public int getInt(String path) {
            return config.getInt(path);
        }

        @Override
        public long getLong(String path) {
            return config.getLong(path);
        }

        @Override
        public double getDouble(String path) {
            return config.getDouble(path);
        }

        @Override
        public String getString(String path) {
            return config.getString(path);
        }

        @Override
        public <T extends Enum<T>> T getEnum(Class<T> enumClass, String path) {
            return config.getEnum(enumClass, path);
        }

        @Override
        public Duration getDuration(String path) {
            return config.getDuration(path);
        }

        @Override
        public Period getPeriod(String path) {
            return config.getPeriod(path);
        }
    }
}
