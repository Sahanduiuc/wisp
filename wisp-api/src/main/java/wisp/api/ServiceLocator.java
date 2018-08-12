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

/**
 * A wrapper around {@link java.util.ServiceLoader} which provides additional
 * methods for looking up known services matching criteria.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface ServiceLocator extends Iterable<ServiceModule> {
    /**
     * Gets the first returned ServiceModule implementing the given service interface.
     */
    <T> T firstImplementing(Class<T> interfaceClazz);

    /**
     * Gets all matching ServiceModules implementing the given service interface.
     */
    <T> Iterable<T> allImplementing(Class<T> interfaceClazz);
}
