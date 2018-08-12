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

package wisp.api;

import java.time.Duration;
import java.time.Period;

/**
 * Typed access to a node in a configuration file. By convention paths are dot-delimited, e.g. wisp.logger.logConfig,
 * but how these paths are expressed in the config file format is implementation-dependent.
 *
 * <p>All methods throw {@link ConfigurationException} if they fail to resolve a path or convert a type.</p>
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface Configuration {
    boolean hasPath(String path);
    boolean getBoolean(String path);
    int getInt(String path);
    long getLong(String path);
    double getDouble(String path);
    String getString(String path);
    <T extends Enum<T>> T getEnum(Class<T> enumClass, String path);
    Duration getDuration(String path);
    Period getPeriod(String path);
}
