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

import wisp.api.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation which tracks registered modules and extracts the supported service interfaces.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
class ServiceModuleSet implements Iterable<ServiceModule>, Configurable, Destroyable {
    private List<ServiceModule> loadedModules = new ArrayList<>();

    void registerServiceModule(ServiceModule smod) {
        loadedModules.add(smod);
    }

    @Override
    @Nonnull
    public Iterator<ServiceModule> iterator() {
        return loadedModules.iterator();
    }

    @Override
    public void configure(@Nonnull Configuration config) {
        for (ServiceModule service : this) {
            service.configure(config);
        }
    }

    @Override
    public void destroy() {
        for (ServiceModule service : this) {
            service.destroy();
        }
    }

    void startAll() {
        for (ServiceModule service : loadedModules) {
            service.start();
        }
    }

    void stopAll() {
        for (ServiceModule service : loadedModules) {
            service.stop();
        }
    }
}
