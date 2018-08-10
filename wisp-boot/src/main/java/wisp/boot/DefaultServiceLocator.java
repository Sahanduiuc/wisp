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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.typesafe.config.Config;
import wisp.api.Configurable;
import wisp.api.Destroyable;
import wisp.api.ServiceLocator;
import wisp.api.ServiceModule;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation which tracks registered modules and extracts the supported service interfaces.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
class DefaultServiceLocator implements ServiceLocator, Configurable, Destroyable {
    private List<ServiceModule> loadedModules = new ArrayList<>();
    private Multimap<Class<?>, ServiceModule> modulesByInterface = HashMultimap.create();

    void registerServiceModule(ServiceModule smod) {
        loadedModules.add(smod);

        Class<?> serviceClazz = smod.getClass();
        do {
            for (Class<?> interfaceClazz : serviceClazz.getInterfaces()) {
                modulesByInterface.put(interfaceClazz, smod);
            }
            serviceClazz = serviceClazz.getSuperclass();
        } while (serviceClazz != null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T firstImplementing(@Nonnull Class<T> interfaceClazz) {
        return (T) modulesByInterface.get(interfaceClazz).iterator().next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Iterator<T> allImplementing(@Nonnull Class<T> interfaceClazz) {
        return (Iterator<T>) modulesByInterface.get(interfaceClazz).iterator();
    }

    @Override
    @Nonnull
    public Iterator<ServiceModule> iterator() {
        return loadedModules.iterator();
    }

    @Override
    public void configure(@Nonnull Config config) {
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

    void linkAll() {
        for (ServiceModule service : loadedModules) {
            service.link(this);
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
