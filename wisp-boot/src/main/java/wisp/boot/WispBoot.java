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

import wisp.api.Destroyable;
import wisp.api.ServiceModule;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * TODO: add comment
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
        locator.startAll();

        System.out.println("[wisp] Primordial boot layer modules:");
        for (var module : ModuleLayer.boot().modules()) {
            if (!module.getName().startsWith("jdk") && !module.getName().startsWith("java")) {
                System.out.println("         " + module.getName());
            }
        }

        Path smlibPath = Paths.get(baseDir, "modules").toAbsolutePath().normalize();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(smlibPath, file -> Files.isDirectory(file))) {
            for (Path path : stream) {
                var smName = path.normalize().getFileName().toString();

                System.out.println("\n--------------------------------------------------------------------------------------------------------------------");
                System.out.println("[" + smName + "] Initiating layer for ServiceModule: " + smName);
                System.out.println("[" + smName + "] Loading modules from " + path.normalize().toString());

                var rootModuleName = path.getFileName().toString();

                var modSearchPath = Paths.get(path.toString(), "mlib");
                System.out.println("[" + smName + "] Searching for root module in " + modSearchPath);
                var finder = ModuleFinder.of(modSearchPath);
                var result = finder.find(rootModuleName);
                if (!result.isPresent()) {
                    System.err.println("[" + smName + "] Error: Root module " + rootModuleName + " not found.");
                } else {
                    // Create Configuration based on the root module
                    var cf = ModuleLayer.boot().configuration().resolve
                            (finder, ModuleFinder.of(), Set.of(rootModuleName));

                    // Create new Jigsaw Layer with configuration and ClassLoader
                    var layer = ModuleLayer.boot().defineModulesWithOneLoader(cf, ClassLoader.getSystemClassLoader());

                    System.out.println("[" + smName + "] Created layer containing the following modules:");
                    for (var module : layer.modules()) {
                        System.out.println("         " + module.getName());
                    }
                    for (var smod : ServiceLoader.load(layer, ServiceModule.class)) {
                        smod.start();
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @SuppressWarnings("unused")
    void stopAll() {
        locator.stopAll();
    }

    @Override
    public void destroy() {

    }
}
