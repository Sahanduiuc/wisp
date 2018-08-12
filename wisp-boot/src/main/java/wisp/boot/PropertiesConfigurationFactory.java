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

import wisp.api.Configuration;
import wisp.api.ConfigurationFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Period;
import java.util.Properties;

/**
 * Implementation of config file parsing based on simple Java Properties format.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class PropertiesConfigurationFactory implements ConfigurationFactory {
    @Override
    public boolean canHandle(Path sourceFilePath) {
        return sourceFilePath.endsWith(".properties");
    }

    @Override
    public Configuration parse(Path sourceFilePath) throws IOException {
        var in = new FileInputStream(sourceFilePath.toFile());
        Properties props = new Properties();
        props.load(in);
        return new PropertiesConfiguration(props);
    }

    private class PropertiesConfiguration implements Configuration {
        private final Properties props;

        private PropertiesConfiguration(Properties props) {
            this.props = props;
        }

        @Override
        public boolean hasPath(String path) {
            return props.containsKey(path);
        }

        @Override
        public boolean hasPathOrNull(String path) {
            return hasPath(path) || getString(path).isEmpty();
        }

        @Override
        public boolean getBoolean(String path) {
            return Boolean.parseBoolean(getString(path));
        }

        @Override
        public int getInt(String path) {
            return Integer.parseInt(getString(path));
        }

        @Override
        public long getLong(String path) {
            return Long.parseLong(getString(path));
        }

        @Override
        public double getDouble(String path) {
            return Double.parseDouble(getString(path));
        }

        @Override
        public String getString(String path) {
            return props.getProperty(path);
        }

        @Override
        public <T extends Enum<T>> T getEnum(Class<T> enumClass, String path) {
            return Enum.valueOf(enumClass, getString(path));
        }

        @Override
        public Duration getDuration(String path) {
            return Duration.parse(getString(path));
        }

        @Override
        public Period getPeriod(String path) {
            return Period.parse(getString(path));
        }
    }
}
