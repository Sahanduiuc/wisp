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
import java.util.ServiceLoader;

/**
 * TODO: add comment
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
class DefaultServiceLocator implements ServiceLocator, Configurable, Destroyable {
    private List<ServiceModule> loadedModules = new ArrayList<>();
    private Multimap<Class<?>, ServiceModule> modulesByInterface = HashMultimap.create();

    DefaultServiceLocator() {
        // load all services on the modulepath and cache them
        for (ServiceModule service : ServiceLoader.load(ServiceModule.class)) {
            loadedModules.add(service);

            Class<?> serviceClazz = service.getClass();
            do {
                for (Class<?> interfaceClazz : serviceClazz.getInterfaces()) {
                    modulesByInterface.put(interfaceClazz, service);
                }
                serviceClazz = serviceClazz.getSuperclass();
            } while (serviceClazz != null);
        }

        // resolve dynamic linkages between services
        for (ServiceModule service : this) {
            service.link(this);
        }
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

    void startAll() {
        for (ServiceModule service : loadedModules) {
            System.out.println("start: " + service);
            service.start();
        }
    }

    void stopAll() {
        for (ServiceModule service : loadedModules) {
            service.stop();
        }
    }
}
