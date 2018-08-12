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
import wisp.api.Destroyable;
import wisp.api.ServiceModule;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main entry point for the Wisp microservices server. WispBoot takes care of loading all {@link ServiceModule}
 * instances from a directory provided on the command line and manages their lifecycles: linking, configuring
 * and then starting each discovered {@link ServiceModule}.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class WispBoot implements Destroyable {
    private DefaultServiceLocator locator;

    public static void main(String[] args) {
        new WispBoot().startAll(args[0]);
    }

    @SuppressWarnings("WeakerAccess")
    void startAll(String baseDir) {
        locator = new DefaultServiceLocator();
        var moduleDirs = new ArrayList<ServiceModuleDir>();
        var smlibPath = Paths.get(baseDir, "modules").toAbsolutePath().normalize();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(smlibPath, file -> Files.isDirectory(file))) {
            for (Path path : stream) {
                moduleDirs.add(new ServiceModuleDir(path));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var rootModuleNames = new TreeSet<String>();
        var paths = new Path[moduleDirs.size()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = moduleDirs.get(i).getPath();
            rootModuleNames.add(moduleDirs.get(i).getRootModuleName());
        }
        var finder = ModuleFinder.of(paths);
        var cf = ModuleLayer.boot().configuration().resolve(finder, ModuleFinder.of(), rootModuleNames);

        // Create new Jigsaw Layer with configuration and ClassLoader
        var layer = ModuleLayer.boot().defineModulesWithOneLoader(cf, ClassLoader.getSystemClassLoader());

        System.out.println("Created layer containing the following modules:");
        for (var module : layer.modules()) {
            var moduleName = module.getName();
            if (rootModuleNames.contains(moduleName)) {
                System.out.println("  * " + moduleName);
            } else {
                System.out.println("  - " + moduleName);
            }
        }
        for (var smod : ServiceLoader.load(layer, ServiceModule.class)) {
            locator.registerServiceModule(smod);
        }

        // interim solution before we handle command line options for configuration
        Config config = ConfigFactory.load(ClassLoader.getSystemClassLoader(), "boot.conf");
        locator.configure(new HoconConfigurationFactory().create(config));

        locator.linkAll();
        locator.startAll();
    }

    @SuppressWarnings("unused")
    void stopAll() {
        locator.stopAll();
    }

    @Override
    public void destroy() {

    }

    private class ServiceModuleDir {
        private final Path path;
        private final String rootModuleName;

        private ServiceModuleDir(Path path) {
            this.path = path;
            this.rootModuleName = path.normalize().getFileName().toString();
        }

        private Path getPath() {
            return path;
        }

        private String getRootModuleName() {
            return rootModuleName;
        }
    }
}
